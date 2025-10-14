package petlifecycle.core.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.cloud.aws.s3")
@Data
@Component
public class S3Properties {
    private String bucket;
    private String region = "ap-southeast-2";
    private String baseUrl;
}
