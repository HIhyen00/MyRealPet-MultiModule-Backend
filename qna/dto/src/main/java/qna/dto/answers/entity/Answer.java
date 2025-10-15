package qna.dto.answers.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Long userId;

    @Lob @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isPrivate = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AnswerStatus status = AnswerStatus.VISIBLE;

    private int upvoteCount = 0;
    private int downvoteCount = 0;
    private int reportCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
