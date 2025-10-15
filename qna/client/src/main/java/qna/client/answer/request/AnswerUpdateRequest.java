package qna.client.answer.request;

import jakarta.validation.constraints.NotBlank;

public record AnswerUpdateRequest(
        @NotBlank String content,
        boolean isPrivate
) {
}
