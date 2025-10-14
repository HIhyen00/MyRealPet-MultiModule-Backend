package petlifecycle.core.metadata.service;

import petlifecycle.core.metadata.repository.MetaDataFileRepository;
import petlifecycle.dto.metadata.entity.AccessType;
import petlifecycle.dto.metadata.entity.FileType;
import petlifecycle.dto.metadata.entity.MetaDataFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FileService {
    private final S3Service s3Service;
    private final MetaDataFileRepository metaDataFileRepository;

    public MetaDataFile uploadFile(MultipartFile file, FileType fileType, Long accountId,
                                   AccessType accessType, String relatedEntityType, Long relatedEntityId) {

        // 파일 유효성 검증
        validateFile(file, fileType);

        // 저장할 파일명 생성 (중복 방지)
        String storedFileName = generateStoredFileName(file.getOriginalFilename());

        // S3 키 생성 (디렉토리 구조)
        String s3Key = generateS3Key(fileType, storedFileName);

        try {
            // S3에 파일 업로드
            String fileUrl = s3Service.uploadFile(file, s3Key);

            // 메타데이터 저장
            MetaDataFile metaDataFile = new MetaDataFile(
                    accountId,
                    file.getOriginalFilename(),
                    storedFileName,
                    s3Key,
                    file.getContentType(),
                    file.getSize(),
                    fileType,
                    accessType,
                    relatedEntityType,
                    relatedEntityId
            );

            return metaDataFileRepository.save(metaDataFile);
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}" ,e.getMessage());
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }

    }

    public MetaDataFile getFileById(Long fileId) {
        return metaDataFileRepository.findById(fileId)
                .orElseThrow(()-> new IllegalArgumentException("파일을 찾을 수 없습니다."));
    }

    public String getFileUrl(String s3Key) {
        MetaDataFile file = metaDataFileRepository.findByS3Key(s3Key)
                .orElseThrow(()-> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        if (file.getAccessType() == AccessType.PRIVATE) {
            return s3Service.generatePresignedUrl(s3Key, 60);
        } else {
            return s3Service.getPublicUrl(s3Key);
        }
    }

    public void deleteFile(Long fileId) {
        MetaDataFile metaDataFile = metaDataFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        try {
            s3Service.deleteFile(metaDataFile.getS3Key());

            metaDataFile.delete();
            metaDataFileRepository.save(metaDataFile);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}" ,e.getMessage());
            throw new IllegalArgumentException("파일 삭제에 실패했습니다.");
        }
    }
    // s3, metadata soft delete
    public void softDeleteFile(Long fileId) {
        MetaDataFile metaDataFile = metaDataFileRepository.findById(fileId)
                .orElseThrow(()-> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        metaDataFile.delete();
        metaDataFileRepository.save(metaDataFile);
        log.info("파일 소프트 삭제 완료: {}" ,fileId);
    }

    // s3 hard delete, metadata soft delete
    public void hardDeleteFile(Long fileId) {
        MetaDataFile metaDataFile = metaDataFileRepository.findById(fileId)
                .orElseThrow(()-> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        try {
            s3Service.deleteFile(metaDataFile.getS3Key());
            metaDataFile.s3Delete();

            metaDataFile.delete();
            metaDataFileRepository.save(metaDataFile);
            log.info("파일 하드 삭제 완료: {}" ,fileId);
        } catch (Exception e) {
            log.error("파일 하드 삭제 실패: {}" ,e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.");
        }
    }

    public void restoreFile(Long fileId) {
        MetaDataFile metaDataFile = metaDataFileRepository.findById(fileId)
                .orElseThrow(()-> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        if (metaDataFile.getS3Deleted()) {
            throw new IllegalArgumentException("S3에서 삭제된 파일을 복구할 수 없습니다.");
        }

        metaDataFile.restore();
        metaDataFileRepository.save(metaDataFile);

        log.info("파일 복구 완료: {}" ,fileId);
    }

    // soft delete 상태 30일 경과 후, s3 hard delete
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupS3File() {
        LocalDateTime cutOfDate = LocalDateTime.now().minusDays(30);

        List<MetaDataFile> filesToClean = metaDataFileRepository
                .findByIsDeletedTrueAndS3DeletedFalseAndDeletedAtBefore(cutOfDate);

        log.info("S3 삭제 파일: {} 개", filesToClean.size());

        int successCount = 0;
        int failedCount = 0;
        for (MetaDataFile file : filesToClean) {
            try {
                s3Service.deleteFile(file.getS3Key());
                file.delete();
                metaDataFileRepository.save(file);

                successCount++;
                log.debug("S3 파일 정리 완료: fileId={}, s3Key={}", file.getId(), file.getS3Key());
            } catch (Exception e) {
                failedCount++;
                log.debug("S3 파일 정리 실패: fileId={}, s3Key={}", file.getId(), file.getS3Key());
            }
        }

        log.info("S3 정리 완료: 성공={}, 실패={}", successCount, failedCount);

    }

    public void validateFile(MultipartFile file, FileType fileType) {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        if(file.getSize() > fileType.getMaxFileSize()) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다.");
        }
        if(!fileType.getAllowedContentTypes().contains(file.getContentType())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }
    }

    private String generateStoredFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    private String generateS3Key(FileType fileType, String storedFileName) {
        String prefix = fileType.getFileTypePrefix();

        return prefix + storedFileName;
    }
}
