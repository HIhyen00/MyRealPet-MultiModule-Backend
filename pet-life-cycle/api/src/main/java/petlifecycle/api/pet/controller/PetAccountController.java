package petlifecycle.api.pet.controller;

import petlifecycle.client.pet.request.RegisterPetAccountRequest;
import petlifecycle.client.pet.request.UpdatePetAccountRequest;
import petlifecycle.client.pet.response.ListPetAccountResponse;
import petlifecycle.client.pet.response.ReadPetAccountResponse;
import petlifecycle.client.pet.response.RegisterPetAccountResponse;
import petlifecycle.client.pet.response.UpdatePetAccountResponse;
import petlifecycle.core.pet.service.PetAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/profiles")
public class PetAccountController {

    private final PetAccountService petAccountService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegisterPetAccountResponse> register(@ModelAttribute RegisterPetAccountRequest request, @RequestAttribute("userId") Long userId) {

        try {
            log.info("Registering pet account: {}", request);
            RegisterPetAccountResponse response = petAccountService.registerPetAccount(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("펫 등록 실패: {}", e.getMessage());
            throw new RuntimeException("펫 등록에 실패했습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<ListPetAccountResponse> list(@RequestAttribute("userId") Long userId) {

        try {
            log.info("Reading pet account: {}", userId);
            ListPetAccountResponse response = petAccountService.listPetAccount(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("펫 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("펫 정보를 불러오는데 실패했습니다.");
        }
    }

    @GetMapping("/{petId}")
    public ResponseEntity<ReadPetAccountResponse> read(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {

        try {
            log.info("Reading pet account: {}", petId);
            ReadPetAccountResponse response = petAccountService.readPetAccount(userId, petId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("펫 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("펫 정보를 불러오는데 실패했습니다.");
        }
    }


    @PutMapping(value = "/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdatePetAccountResponse> update(@PathVariable("petId") Long petId, @ModelAttribute UpdatePetAccountRequest request,
                                                           @RequestAttribute("userId") Long userId) {

        try {
            log.info("Registering pet account: {}", request);
            UpdatePetAccountResponse response = petAccountService.updatePetAccount(userId, petId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("펫 수정 실패: {}", e.getMessage());
            throw new RuntimeException("펫 정보 수정에 실패했습니다.");
        }
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<String> delete(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {

        try {
            petAccountService.deletePetAccount(userId, petId);
            return ResponseEntity.ok("펫 삭제에 성공했습니다.");
        } catch (Exception e) {
            log.error("펫 삭제 실패 (ID: {}): {}", petId, e.getMessage(), e);
            throw new RuntimeException("펫 삭제에 실패했습니다.");
        }
    }
}
