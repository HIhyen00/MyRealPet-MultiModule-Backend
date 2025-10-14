package petlifecycle.api.metadata.controller;

import petlifecycle.client.metadata.response.FileUploadResponse;
import petlifecycle.core.metadata.service.FileService;
import petlifecycle.dto.metadata.entity.AccessType;
import petlifecycle.dto.metadata.entity.FileType;
import petlifecycle.dto.metadata.entity.MetaDataFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/files")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") FileType fileType,
            @RequestParam(value = "accessType", defaultValue = "PRIVATE") AccessType accessType,
            @RequestParam(value = "relatedEntityType", required = false) String relatedEntityType,
            @RequestParam(value = "relatedEntityId", required = false) Long relatedEntityId,
            @RequestAttribute("userId") Long userId) {

        try {
            MetaDataFile uploadedFile = fileService.uploadFile(
                    file, fileType, userId, accessType, relatedEntityType, relatedEntityId);

            String fileUrl = fileService.getFileUrl(uploadedFile.getS3Key());

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileId(uploadedFile.getId())
                    .originalFileName(uploadedFile.getOriginalFileName())
                    .fileUrl(fileUrl)
                    .fileSize(uploadedFile.getFileSize())
                    .contentType(uploadedFile.getContentType())
                    .message("파일이 성공적으로 업로드되었습니다.")
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(FileUploadResponse.builder()
                            .message("파일 업로드에 실패했습니다. ")
                            .build());
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@RequestAttribute("userId") Long userId, @PathVariable Long fileId) {

        try {
            MetaDataFile file = fileService.getFileById(fileId);

            if (!file.getAccountId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일을 삭제할 권한이 없습니다.");
            }
            fileService.deleteFile(fileId);
            return ResponseEntity.ok("파일이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body("파일 삭제에 실패했습니다.");
        }
    }
}
