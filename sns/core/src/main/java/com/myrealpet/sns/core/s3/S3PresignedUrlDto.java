package com.myrealpet.sns.core.s3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class S3PresignedUrlDto {
    private String presignedUrl;
    private String fileUrl;
}
