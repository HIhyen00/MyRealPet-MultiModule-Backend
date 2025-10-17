package petlifecycle.core.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import petlifecycle.core.config.ai.OpenAIProperties;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenAIService {

    private final WebClient webClient;
    private final OpenAIProperties openAIProperties;

    // WebClient를 생성자 주입으로 구성
    public OpenAIService(OpenAIProperties openAIProperties) {
        this.openAIProperties = openAIProperties;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * AI에게 질문을 보내고 답변을 받아오는 함수
     */
    public Mono<String> getChatResponse(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", openAIProperties.getModel(),
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.7
        );

        return webClient.post()
                .uri(openAIProperties.getUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAIProperties.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 이미지 분석
    public Mono<String> analyzeReceiptImage(String base64Image, String prompt) {
        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", prompt
        );

        Map<String, Object> imageContent = Map.of(
                "type", "image_url",
                "image_url", Map.of(
                        "url", "data:image/jpeg;base64," + base64Image,
                        "detail", "high"
                )
        );

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", List.of(imageContent)
        );

        Map<String, Object> requestBody = Map.of(
                "model", openAIProperties.getModel(),
                "messages", List.of(systemMessage, userMessage),
                "max_tokens", 1024,
                "temperature", 0.2
        );

        log.info("OpenAI API 호출: model = {}", openAIProperties.getModel());
        return webClient.post()
                .uri(openAIProperties.getUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAIProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("OpenAI API 오류: status={}, body={}",
                                            response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException(
                                            "OpenAI API 호출 실패: " + errorBody));
                                })
                )
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                    log.info("OpenAI Vision API 호출 성공");
                    log.info("👀GPT 원본 응답: {}", response);
                })
                .doOnError(error -> log.error("OpenAI Vision API 호출 중 오류: {}", error.getMessage()))
                .timeout(Duration.ofSeconds(60))  // 60초 타임아웃
                .retry(2);
    }
}
