package qna.client.answer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerCreateRequest(
        @NotNull Long questionId,
        @NotBlank String content,
        boolean isPrivate
) {
}
