package qna.dto.questions.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "questions",
        indexes = {
                @Index(name = "idx_questions_user", columnList = "user_id"),
                @Index(name = "idx_questions_status", columnList = "status"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 질문 작성자 (User 엔티티와의 관계는 팀 정책에 맞춰 추후 연동) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 200, nullable = false)
    private String title;

    @Lob @Column(nullable = false)
    private String content;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private QuestionStatus status = QuestionStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // 질문 통계
    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private QuestionStat stats;

    public void assignStats(QuestionStat stats) {
        this.stats = stats;
        stats.setQuestion(this);
    }

}
