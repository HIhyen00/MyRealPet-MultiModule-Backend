package qna.client;

import jakarta.validation.constraints.Size;

public record ReportRequest(
        @Size(max=255) String reason
) {
}
