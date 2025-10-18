package com.myrealpet.common.s3;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 설정 프로퍼티
 * application.yml의 spring.cloud.aws.s3 설정을 바인딩
 */
@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
@Data
@Component
@ConditionalOnClass(S3Client.class)
public class S3Properties {
    private String bucket;
    private String region = "ap-southeast-2";
    private String baseUrl;
}
