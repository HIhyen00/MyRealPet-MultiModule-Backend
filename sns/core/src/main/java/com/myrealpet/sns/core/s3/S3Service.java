package com.myrealpet.sns.core.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 이미지 업로드
     * @param multipartFile 업로드할 파일
     * @param dirName 업로드할 디렉토리 이름
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(MultipartFile multipartFile, String dirName) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + System.currentTimeMillis() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
        );

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에서 파일 삭제
     * @param fileUrl 삭제할 파일의 URL
     */
    public void deleteFile(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
    }

    /**
     * 프리사인드 URL 생성
     * @param key 파일 키
     * @return 프리사인드 URL과 파일 URL
     */
    public S3PresignedUrlDto generatePresignedUrl(String key) {
        String fileUrl = amazonS3.getUrl(bucket, key).toString();
        
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1시간
        expiration.setTime(expTimeMillis);
        
        String presignedUrl = amazonS3.generatePresignedUrl(bucket, key, expiration).toString();
        
        return new S3PresignedUrlDto(presignedUrl, fileUrl);
    }
}
