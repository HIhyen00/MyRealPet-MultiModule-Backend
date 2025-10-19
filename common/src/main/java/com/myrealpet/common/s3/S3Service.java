package com.myrealpet.common.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.UUID;

/**
 * 통합 S3 서비스 (AWS SDK v2)
 * 모든 모듈에서 공통으로 사용하는 S3 파일 업로드/다운로드/삭제 기능 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnClass(S3Client.class)
@ConditionalOnBean(S3Client.class)
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    @PostConstruct
    public void init() {
        log.info("🪣 Common S3Service 초기화:");
        log.info("  - Bucket: {}", s3Properties.getBucket());
        log.info("  - Region: {}", s3Properties.getRegion());
        log.info("  - BaseUrl: {}", s3Properties.getBaseUrl());

        if (s3Properties.getBucket() == null || s3Properties.getBucket().isEmpty()) {
            log.error("❌ S3 버킷이 설정되지 않았습니다!");
        }
    }

    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param s3Key S3에 저장할 키 (경로 포함)
     * @return 업로드된 파일의 공개 URL
     */
    public String uploadFile(MultipartFile file, String s3Key) {
        log.info("📤 S3 업로드 시작:");
        log.info("  - Bucket: {}", s3Properties.getBucket());
        log.info("  - S3 Key: {}", s3Key);
        log.info("  - 파일명: {}", file.getOriginalFilename());
        log.info("  - 크기: {} bytes", file.getSize());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = getPublicUrl(s3Key);
            log.info("✅ S3 업로드 성공: {}", fileUrl);

            return fileUrl;
        } catch (S3Exception e) {
            log.error("❌ S3 업로드 실패:");
            log.error("  - Status Code: {}", e.statusCode());
            log.error("  - Error Code: {}", e.awsErrorDetails().errorCode());
            log.error("  - Error Message: {}", e.awsErrorDetails().errorMessage());
            log.error("  - Bucket: {}", s3Properties.getBucket());
            throw new IllegalArgumentException("파일 업로드에 실패했습니다: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("❌ S3 파일 업로드 실패: {}", e.getMessage(), e);
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }
    }

    /**
     * 파일 업로드 (디렉토리 지정, UUID 자동 생성)
     * @param file 업로드할 파일
     * @param dirName 디렉토리 이름 (예: "sns/post")
     * @return 업로드된 파일의 공개 URL
     */
    public String uploadFileToDirectory(MultipartFile file, String dirName) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + System.currentTimeMillis() + extension;

        return uploadFile(file, fileName);
    }

    /**
     * 파일 다운로드
     * @param s3Key S3 키 (경로 포함)
     * @return 파일 바이트 배열
     */
    public byte[] downloadFile(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();

        } catch (Exception e) {
            log.error("S3 파일 다운로드 실패: {}", e.getMessage());
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
    }

    /**
     * 파일 삭제
     * @param s3Key S3 키 (경로 포함)
     */
    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("✅ S3 파일 삭제 성공: {}", s3Key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage());
            throw new IllegalArgumentException("파일 삭제에 실패했습니다.");
        }
    }

    /**
     * URL에서 파일 삭제
     * @param fileUrl 파일 URL
     */
    public void deleteFileByUrl(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(s3Properties.getBucket()) + s3Properties.getBucket().length() + 1);
        deleteFile(key);
    }

    /**
     * 공개 URL 생성
     * @param s3Key S3 키 (경로 포함)
     * @return 공개 URL
     */
    public String getPublicUrl(String s3Key) {
        if (s3Properties.getBaseUrl() != null && !s3Properties.getBaseUrl().isEmpty()) {
            return s3Properties.getBaseUrl() + "/" + s3Key;
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucket(),
                s3Properties.getRegion(),
                s3Key);
    }

    /**
     * Presigned URL 생성 (임시 접근 URL)
     * @param s3Key S3 키 (경로 포함)
     * @param expiration 만료 시간 (분)
     * @return Presigned URL
     */
    public String generatePresignedUrl(String s3Key, Integer expiration) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.getMessage());
            throw new IllegalArgumentException("URL 생성에 실패했습니다.");
        }
    }

    /**
     * Presigned URL과 파일 URL을 함께 반환 (DTO 버전)
     * @param s3Key S3 키 (경로 포함)
     * @return S3PresignedUrlDto (presignedUrl + fileUrl)
     */
    public S3PresignedUrlDto generatePresignedUrl(String s3Key) {
        String fileUrl = getPublicUrl(s3Key);
        String presignedUrl = generatePresignedUrl(s3Key, 60); // 기본 60분
        return new S3PresignedUrlDto(presignedUrl, fileUrl);
    }
}
