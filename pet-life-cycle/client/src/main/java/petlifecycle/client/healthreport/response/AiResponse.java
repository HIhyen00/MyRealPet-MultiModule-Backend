package petlifecycle.client.healthreport.response;

import lombok.Getter;

@Getter
public class AiResponse {
    private String answer;

    public AiResponse(String answer) {
        this.answer = answer;
    }
}
