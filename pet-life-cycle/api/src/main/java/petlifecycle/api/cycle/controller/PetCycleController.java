package petlifecycle.api.cycle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petlifecycle.client.cycle.request.PetCycleCreateRequest;
import petlifecycle.client.cycle.response.PetCycleResponse;
import petlifecycle.core.cycle.service.PetCycleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/{petId}/cycles")
public class PetCycleController {

    private final PetCycleService petCycleService;

    @PostMapping
    public ResponseEntity<Void> createPetCycle(@PathVariable Long petId, @RequestBody PetCycleCreateRequest request,
                                               @RequestAttribute("userId") Long userId) {
        petCycleService.saveCycle(petId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<PetCycleResponse>> getPetCycles(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {
        List<PetCycleResponse> cycles = petCycleService.findCycles(petId, userId);
        return ResponseEntity.ok(cycles);
    }

    @DeleteMapping("/{cycleId}")
    public ResponseEntity<Void> deletePetCycle(@PathVariable Long petId, @PathVariable Long cycleId,
                                               @RequestAttribute("userId") Long userId) {
        // petId는 인가(Authorization)에 사용할 수 있으나, 현재 로직에서는 cycleId만으로 삭제 처리
        petCycleService.deleteCycle(cycleId, petId, userId);
        return ResponseEntity.noContent().build();
    }
}
