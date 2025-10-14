package petlifecycle.api.missionreport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petlifecycle.client.missionreport.request.CreateDailyMissionRequest;
import petlifecycle.client.missionreport.response.DailyMissionResponse;
import petlifecycle.core.missionreport.service.PetDailyMissionService;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetDailyMissionController {

    private final PetDailyMissionService petDailyMissionService;

    @PostMapping("/{petId}/daily-missions")
    public ResponseEntity<DailyMissionResponse> createDailyMission(
            @PathVariable Long petId,
            @RequestBody CreateDailyMissionRequest request,
            @RequestAttribute("userId") Long userId) {
        DailyMissionResponse response = petDailyMissionService.createDailyMission(petId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{petId}/daily-missions")
    public ResponseEntity<List<DailyMissionResponse>> getDailyMissions(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {
        List<DailyMissionResponse> response = petDailyMissionService.getDailyMissions(petId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{missionId}/complete")
    public ResponseEntity<Void> completeDailyMission(@PathVariable Long missionId, @RequestAttribute("userId") Long userId) {
        petDailyMissionService.completeDailyMission(missionId);
        return ResponseEntity.ok().build();
    }
}
