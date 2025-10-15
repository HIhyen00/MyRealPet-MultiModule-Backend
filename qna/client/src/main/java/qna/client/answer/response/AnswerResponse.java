package qna.client.answer.response;

import qna.dto.answers.entity.Answer;
import qna.dto.answers.entity.AnswerStatus;

import java.time.LocalDateTime;

public record AnswerResponse(
        Long id,
        Long questionId,
        Long userId,
        String content,
        boolean isPrivate,
        AnswerStatus status,
        int upvoteCount,
        int downvoteCount,
        int reportCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // 목록/상세 기본 정책(그대로 노출)
    public static AnswerResponse of(Answer a) {
        return of(a, true);
    }

    // 필요 시 본문 노출 제어용
    public static AnswerResponse of(Answer a, boolean showBody) {
        if (a == null) return null;
        return new AnswerResponse(
                a.getId(),
                a.getQuestionId(),
                a.getUserId(),
                showBody ? a.getContent() : null,
                a.isPrivate(),
                a.getStatus(),
                a.getUpvoteCount(),
                a.getDownvoteCount(),
                a.getReportCount(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}