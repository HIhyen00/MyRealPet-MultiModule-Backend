package petlifecycle.api.vaccination.controller;

import petlifecycle.client.vaccination.request.RegisterVacRecordRequest;
import petlifecycle.client.vaccination.request.UpdateVacRecordRequest;
import petlifecycle.client.vaccination.response.ListVacRecordResponse;
import petlifecycle.client.vaccination.response.ReadVacRecordResponse;
import petlifecycle.core.vaccination.service.VaccinationRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/{petId}/vac-records")
public class VaccinationRecordController {

    private final VaccinationRecordService vaccinationRecordService;

    @PostMapping
    public ResponseEntity<String> registerVaccinationRecord(@PathVariable Long petId, @RequestBody RegisterVacRecordRequest request,
                                                            @RequestAttribute("userId") Long userId) {

        try {
            log.info("Registering vaccination record for account {}", userId);
            vaccinationRecordService.registerVacRecord(userId, petId, request);
            return ResponseEntity.ok("접종 이력 등록 성공 accountId: "+ userId + ", petId: " + petId);
        } catch (Exception e) {
            log.error("접종 이력 등록 실패: {}", e.getMessage());
            throw new RuntimeException("접종 이력 등록에 실패했습니다. accountId: "+ userId + ", petId: " + petId);
        }
    }

    // 수정 시, 기록을 불러오기 위함
    @GetMapping("/{recordId}")
    public ResponseEntity<ReadVacRecordResponse> readVaccinationRecord(@PathVariable Long petId, @PathVariable Long recordId,
                                                                       @RequestAttribute("userId") Long userId) {
        try {
            log.info("Reading vaccination record for account {}", userId);
            ReadVacRecordResponse response = vaccinationRecordService.readVacRecord(userId, petId, recordId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("{} 접종 이력 조회 실패: {}",recordId, e.getMessage());
            throw new RuntimeException(recordId +" 접종 이력 조회에 실패했습니다. accountId: "+ userId + ", petId: " + petId);
        }
    }


    // 백신 기록 조회
    @GetMapping
    public ResponseEntity<ListVacRecordResponse> listVaccinationRecord(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {
        try {
            log.info("Listing vaccination record for account {}", userId);
            ListVacRecordResponse response = vaccinationRecordService.listVacRecords(userId, petId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("접종 이력 조회 실패: {}", e.getMessage());
            throw new RuntimeException("접종 이력 조회에 실패했습니다. accountId: "+ userId + ", petId: " + petId);
        }
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<String> updateVaccinationRecord(@PathVariable Long petId, @PathVariable Long recordId,
                                                          @RequestBody UpdateVacRecordRequest request, @RequestAttribute("userId") Long userId) {
        try {
            log.info("Updating vaccination record for account {}", userId);
            vaccinationRecordService.updateVaccinationRecord(userId, petId, recordId, request);
            return ResponseEntity.ok("접종 이력 수정 완료 accountId: "+ userId + ", petId: " + petId);
        } catch (Exception e) {
            log.error("접종 이력 수정 실패: {}", e.getMessage());
            throw new RuntimeException("접종 이력 수정에 실패했습니다. accountId: "+ userId + ", petId: " + petId);
        }
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<String> deleteVaccinationRecord(@PathVariable Long petId, @PathVariable Long recordId,
                                                          @RequestAttribute("userId") Long userId) {
        try {
            log.info("Deleting vaccination record for account {}", userId);
            vaccinationRecordService.deleteVacRecord(userId, petId, recordId);
            return ResponseEntity.ok("접종 이력 삭제 완료 accountId: "+ userId + ", petId: " + petId);
        } catch (Exception e) {
            log.error("접종 이력 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("접종 이력 삭제에 실패했습니다. accountId: "+ userId + ", petId: " + petId);
        }
    }
}
