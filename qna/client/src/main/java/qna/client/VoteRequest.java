package qna.client;

import jakarta.validation.constraints.NotBlank;

public record VoteRequest(
        @NotBlank String type // "up" or "down"
) {
}
