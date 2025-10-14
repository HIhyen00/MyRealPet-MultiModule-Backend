package petlifecycle.core.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAIProperties {
    private String apiKey;
    private String model;
    private String url;
}
