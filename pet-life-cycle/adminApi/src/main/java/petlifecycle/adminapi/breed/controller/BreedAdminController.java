package petlifecycle.adminapi.breed.controller;

import petlifecycle.client.breed.request.ListBreedRequest;
import petlifecycle.client.breed.request.RegisterBreedRequest;
import petlifecycle.client.breed.request.UpdateBreedRequest;
import petlifecycle.client.breed.response.ListBreedResponse;
import petlifecycle.client.breed.response.ReadBreedResponse;
import petlifecycle.client.breed.response.RegisterBreedResponse;
import petlifecycle.client.breed.response.UpdateBreedResponse;
import petlifecycle.core.breed.service.BreedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/pets/breeds")
public class BreedAdminController {

    private final BreedService breedService;

    @PostMapping
    public ResponseEntity<RegisterBreedResponse> register(@RequestBody RegisterBreedRequest request,
                                                          @RequestAttribute("userId") Long adminId) {
        try {
            log.info("Registering breed: {}", request);
            RegisterBreedResponse response = breedService.registerBreed(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("품종 등록 실패: {}", e.getMessage());
            throw new RuntimeException("품종 등록에 실패했습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<ListBreedResponse> list(@ModelAttribute ListBreedRequest request,
                                                  @RequestAttribute("userId") Long adminId) {
        try {
            ListBreedResponse response = breedService.getAllBreed(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("품종 목록 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("품종 목록 조회에 실패했습니다.");
        }
    }

    @GetMapping("/{breedId}")
    public ResponseEntity<ReadBreedResponse> read(@PathVariable Long breedId,
                                                  @RequestAttribute("userId") Long adminId) {
        try {
            ReadBreedResponse response = breedService.readBreed(breedId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("품종 조회 실패 (ID: {}): {}", breedId, e.getMessage(), e);
            throw new RuntimeException("품종 조회에 실패했습니다.");
        }
    }

    @PutMapping("/{breedId}")
    public ResponseEntity<UpdateBreedResponse> update(@PathVariable Long breedId, @RequestBody UpdateBreedRequest request,
                                                      @RequestAttribute("userId") Long adminId) {
        try {
            UpdateBreedResponse response = breedService.updateBreed(breedId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("품종 수정 실패 (ID: {}): {}", breedId, e.getMessage(), e);
            throw new RuntimeException("품종 수정에 실패했습니다.");
        }
    }

    @DeleteMapping("/{breedId}")
    public ResponseEntity<String> delete(@PathVariable Long breedId,
                                         @RequestAttribute("userId") Long adminId) {
        try {
            breedService.deleteBreed(breedId);
            return ResponseEntity.ok("파일 삭제에 성공했습니다.");
        } catch (Exception e) {
            log.error("품종 삭제 실패 (ID: {}): {}", breedId, e.getMessage(), e);
            throw new RuntimeException("파일 삭제에 실패했습니다.");
        }
    }

}
