package petlifecycle.client.metadata.response;

import petlifecycle.dto.metadata.entity.MetaDataFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FileUploadResponse {
    private final Long fileId;
    private final String originalFileName;
    private final String fileUrl;
    private final Long fileSize;
    private final String contentType;
    private final String message;

    public static FileUploadResponse from(MetaDataFile file, String fileUrl, String message) {
        return new FileUploadResponse(
                file.getId(),
                file.getOriginalFileName(),
                fileUrl,
                file.getFileSize(),
                file.getContentType(),
                message
        );
    }
}
