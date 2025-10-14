package petlifecycle.client.missionreport.response;

import lombok.Getter;
import petlifecycle.dto.missionreport.entity.PetDailyMission;

import java.time.LocalDate;

@Getter
public class DailyMissionResponse {
    private Long id;
    private String missionContent;
    private boolean isCompleted;
    private LocalDate date;

    public DailyMissionResponse(Long id, String missionContent, boolean isCompleted, LocalDate date) {
        this.id = id;
        this.missionContent = missionContent;
        this.isCompleted = isCompleted;
        this.date = date;
    }

    public static DailyMissionResponse from(PetDailyMission dailyMission) {
        return new DailyMissionResponse(
                dailyMission.getId(),
                dailyMission.getMissionContent(),
                dailyMission.isCompleted(),
                dailyMission.getDate()
        );
    }
}
