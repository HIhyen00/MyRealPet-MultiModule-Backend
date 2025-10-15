package qna.dto.questions.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_stat",
        indexes = {
                @Index(name = "idx_stats_view", columnList = "viewCount DESC"),
                @Index(name = "idx_stats_like", columnList = "likeCount DESC"),
                @Index(name = "idx_stats_popularity", columnList = "popularityScore DESC")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionStat {
    @Id
    private Long questionId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="question_id")
    private Question question;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long answerCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double popularityScore = 0.0;

    // 증감 메서드
    public void increaseViewCount() {this.viewCount++;}

    public void increaseLikeCount() {this.likeCount++;}
    public void decreaseLikeCount() { if (this.likeCount > 0) this.likeCount--; }

    public void increaseAnswerCount() {this.answerCount++; }
    public void decreaseAnswerCount() { if (this.answerCount > 0) this.answerCount--; }

    public void recalculatePopularityScore() {
        this.popularityScore = (double) (viewCount + likeCount * 3 + answerCount * 2);
    }
}
