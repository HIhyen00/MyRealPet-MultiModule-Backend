package petlifecycle.client.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FileInfoDto {
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String contentType;
}
