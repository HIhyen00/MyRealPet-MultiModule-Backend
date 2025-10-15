package qna.dto.answers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "answer_reports",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_report_once", columnNames = {"answer_id", "reporter_id"})
        },
        indexes = {
                @Index(name = "idx_reports_answer", columnList = "answer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(length = 255)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
