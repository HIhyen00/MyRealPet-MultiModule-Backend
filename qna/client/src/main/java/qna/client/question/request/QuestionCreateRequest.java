package qna.client.question.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 10000) String content,
        @Size(max = 50) String category
) {
}
