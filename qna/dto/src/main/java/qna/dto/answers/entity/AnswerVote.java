package qna.dto.answers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "answer_votes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_answer_vote", columnNames = {"answer_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_votes_answer", columnList = "answer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="answer_id", nullable=false)
    private Long answerId;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=8)
    private VoteType type;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;
}
