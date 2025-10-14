package petlifecycle.dto.dailymission.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mission_completions")
public class MissionCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ToDo: User 엔티티와 연관관계 매핑 필요
    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private DailyMission dailyMission;

    @Column(nullable = false)
    private LocalDate completedDate;

    public static MissionCompletion create(Long userId, DailyMission dailyMission) {
        MissionCompletion completion = new MissionCompletion();
        completion.userId = userId;
        completion.dailyMission = dailyMission;
        completion.completedDate = LocalDate.now();
        return completion;
    }
}
