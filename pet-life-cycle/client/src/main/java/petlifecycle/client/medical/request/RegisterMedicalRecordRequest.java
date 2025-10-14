package petlifecycle.client.medical.request;

import petlifecycle.client.medical.dto.MedicationItemDto;
import petlifecycle.client.medical.dto.TestItemDto;
import petlifecycle.client.medical.dto.TreatmentItemDto;
import petlifecycle.dto.medical.entity.MedicalRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RegisterMedicalRecordRequest {
    // 행정정보
    private final String hospitalName;
    private final String hospitalNumber;
    private final String hospitalAddress;
    private final LocalDate visitDate;
    private final Integer totalAmount;
    private final Integer vatAmount;

    // 진단 및 증상
    private final String diagnosis;
    private final String symptoms;

    // 청구서 파일 ID
    private final Long receiptFileId;

    // 첨부 파일 IDs
    private final List<Long> attachmentFileIds;

    // 상세 항목들
    private final List<TestItemDto> testItems;
    private final List<TreatmentItemDto> treatmentItems;
    private final List<MedicationItemDto> medicationItems;

    public MedicalRecord toMedicalRecord(Long petId, Long accountId) {
        return new MedicalRecord(
                petId,
                accountId,
                this.hospitalName,
                this.hospitalNumber,
                this.hospitalAddress,
                this.visitDate,
                this.totalAmount,
                this.vatAmount,
                this.receiptFileId,
                this.diagnosis,
                this.symptoms
        );
    }
}
