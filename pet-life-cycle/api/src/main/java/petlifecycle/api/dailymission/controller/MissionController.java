package petlifecycle.api.dailymission.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petlifecycle.client.dailymission.response.DailyMissionResponse;
import petlifecycle.client.dailymission.response.MissionCompletionResponse;
import petlifecycle.client.dailymission.response.MissionStatsResponse;
import petlifecycle.core.dailymission.service.MissionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MissionController {

    private final MissionService missionService;

    // 1. 전체 데일리 미션 목록 조회
    @GetMapping("/missions")
    public ResponseEntity<List<DailyMissionResponse>> getDailyMissions() {
        List<DailyMissionResponse> missions = missionService.getDailyMissions();
        return ResponseEntity.ok(missions);
    }

    // 2. 특정 유저의 월별 미션 완료 기록 조회
    @GetMapping("/{userId}/missions/history")
    public ResponseEntity<List<MissionCompletionResponse>> getMissionHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        List<MissionCompletionResponse> history = missionService.getMissionHistory(userId, year, month);
        return ResponseEntity.ok(history);
    }

    // 3. 미션 완료 처리
    @PostMapping("/{userId}/missions/{missionId}/completions")
    public ResponseEntity<Void> completeMission(@RequestAttribute("userId") Long userId, @PathVariable Long missionId) {
        missionService.completeMission(userId, missionId);
        return ResponseEntity.ok().build();
    }

    // 4. 미션 완료 취소
    @DeleteMapping("/{userId}/missions/{missionId}/completions")
    public ResponseEntity<Void> cancelMissionCompletion(@RequestAttribute("userId") Long userId, @PathVariable Long missionId) {
        missionService.cancelMissionCompletion(userId, missionId);
        return ResponseEntity.noContent().build();
    }

    // 5. 특정 유저의 미션 통계 조회
    @GetMapping("/{userId}/missions/stats")
    public ResponseEntity<MissionStatsResponse> getMissionStats(@PathVariable Long userId) {
        MissionStatsResponse stats = missionService.getMissionStats(userId);
        return ResponseEntity.ok(stats);
    }
}
