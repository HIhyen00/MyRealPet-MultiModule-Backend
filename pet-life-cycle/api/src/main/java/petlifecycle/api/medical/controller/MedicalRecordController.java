package petlifecycle.api.medical.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petlifecycle.client.medical.request.ListMedicalRecordRequest;
import petlifecycle.client.medical.request.RegisterMedicalRecordRequest;
import petlifecycle.client.medical.request.UpdateMedicalRecordRequest;
import petlifecycle.client.medical.response.ListMedicalRecordResponse;
import petlifecycle.client.medical.response.ReadMedicalRecordResponse;
import petlifecycle.client.medical.response.ReceiptAnalysisResponse;
import petlifecycle.core.medical.service.MedicalRecordService;
import petlifecycle.core.medical.service.ReceiptAnalysisService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/{petId}/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final ReceiptAnalysisService receiptAnalysisService;

    @PostMapping
    public ResponseEntity<String> registerMedicalRecord(@PathVariable("petId") Long petId,
                                                        @RequestBody @Valid RegisterMedicalRecordRequest request,
                                                        @RequestAttribute("userId") Long userId) {
        try {
            medicalRecordService.registerMedicalRecord(userId, petId, request);
            return ResponseEntity.ok("진료기록이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("진료기록 등록 실패: {}", e.getMessage());
            throw new RuntimeException("진료기록 등록에 실패했습니다.");
        }
    }

    @GetMapping
    public ResponseEntity<ListMedicalRecordResponse> listMedicalRecord(@PathVariable("petId") Long petId,
                                                                       @ModelAttribute ListMedicalRecordRequest request,
                                                                       @RequestAttribute("userId") Long userId) {
        try {
            ListMedicalRecordResponse response = medicalRecordService.listMedicalRecord(userId, petId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("진료기록 목록 조회 실패: {}", e.getMessage());
            throw new RuntimeException("진료기록 목록 조회에 실패했습니다.");
        }
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<ReadMedicalRecordResponse> readMedicalRecord(@PathVariable("petId") Long petId, @PathVariable Long recordId,
                                                                       @RequestAttribute("userId") Long userId) {
        try {
            ReadMedicalRecordResponse response = medicalRecordService.readMedicalRecord(userId, petId, recordId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("{} 진료기록 조회 실패: {}", recordId, e.getMessage());
            throw new RuntimeException("진료기록 조회에 실패했습니다.");
        }
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<String> updateMedicalRecord(@PathVariable("petId") Long petId, @PathVariable Long recordId,
                                                      @RequestBody @Valid UpdateMedicalRecordRequest request,
                                                      @RequestAttribute("userId") Long userId) {
        try {
            medicalRecordService.updateMedicalRecord(userId, petId, recordId, request);
            return ResponseEntity.ok("진료기록 수정에 성공하셨습니다.");
        } catch (Exception e) {
            log.error("{} 진료기록 수정 실패: {}", recordId, e.getMessage());
            throw new RuntimeException("진료기록 수정에 실패하셨습니다.");
        }
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<String> deleteMedicalRecord(@PathVariable("petId") Long petId, @PathVariable Long recordId,
                                                      @RequestAttribute("userId") Long userId) {
        try {
            medicalRecordService.deleteMedicalRecord(userId, petId, recordId);
            return ResponseEntity.ok("진료기록 삭제에 성공하셨습니다.");
        } catch (Exception e) {
            log.error("{} 진료기록 삭제 실패: {}", recordId, e.getMessage());
            throw new RuntimeException("진료기록 삭제에 실패하셨습니다.");
        }
    }

    @PostMapping("/analyze-receipt")
    public ResponseEntity<?> analyzeReceipt(@PathVariable Long petId, @RequestParam("file") MultipartFile file,
                                            @RequestAttribute("userId") Long userId) {
        try {
            ReceiptAnalysisResponse response = receiptAnalysisService.analyzeReceipt(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("청구서 분석 실패: {}", e.getMessage());
            throw new RuntimeException("청구서 분석에 실패하였습니다.");
        }
    }
}
