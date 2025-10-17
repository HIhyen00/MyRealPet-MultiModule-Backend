package qna.client.question.response;

import lombok.Builder;
import qna.dto.questions.entity.Question;
import qna.dto.questions.entity.QuestionStat;
import qna.dto.questions.entity.QuestionStatus;

import java.time.LocalDateTime;

@Builder
public record QuestionResponse(
        Long id,
        Long userId,
        String title,
        String content,
        String category,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long viewCount,
        Long likeCount,
        Long answerCount,
        Double popularityScore
) {
    public static QuestionResponse of(Question q) {
        QuestionStat s = q.getStats();
        return QuestionResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .userId(q.getUserId())
                .content(q.getContent())
                .category(q.getCategory())
                .status(q.getStatus())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .viewCount(s != null ? s.getViewCount() : 0L)
                .likeCount(s != null ? s.getLikeCount() : 0L)
                .answerCount(s != null ? s.getAnswerCount() : 0L)
                .popularityScore(s != null ? s.getPopularityScore() : 0.0)
                .build();
    }
}

