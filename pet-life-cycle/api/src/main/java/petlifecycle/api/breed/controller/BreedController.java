package petlifecycle.api.breed.controller;

import petlifecycle.client.breed.request.ListBreedRequest;
import petlifecycle.client.breed.response.ListBreedResponse;
import petlifecycle.core.breed.service.BreedService;
import petlifecycle.dto.breed.entity.Species;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/breeds")
public class BreedController {

    private final BreedService breedService;

    @GetMapping("/dropdown")
    public ResponseEntity<ListBreedResponse> dropdown(@RequestParam Species species, @ModelAttribute ListBreedRequest request) {
        try {
            ListBreedResponse response = breedService.getBreedBySpecies(species, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("품종 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("품종 목록 조회에 실패했습니다.");
        }
    }
}
