package petlifecycle.api.weight.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petlifecycle.client.weight.request.PetWeightCreateRequest;
import petlifecycle.client.weight.response.PetWeightResponse;
import petlifecycle.core.weight.service.PetWeightService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/{petId}/weights")
public class PetWeightController {

    private final PetWeightService petWeightService;

    @PostMapping
    public ResponseEntity<Void> createPetWeight(@PathVariable Long petId, @RequestBody PetWeightCreateRequest request,
                                                @RequestAttribute("userId") Long userId) {
        petWeightService.saveWeight(petId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<PetWeightResponse>> getPetWeights(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {
        List<PetWeightResponse> weights = petWeightService.findWeights(petId, userId);
        return ResponseEntity.ok(weights);
    }
}
