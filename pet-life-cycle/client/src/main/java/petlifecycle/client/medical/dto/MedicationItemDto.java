package petlifecycle.client.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petlifecycle.dto.medical.entity.MedicationItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationItemDto {
    private Long id;
    private String name;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
    private String frequency; // 복용 빈도수
    private Integer days; // 처방 일 수
    private String notes;

    public MedicationItem toMedicationItem(Long medicalRecordId) {
        return new MedicationItem(
                medicalRecordId,
                this.name,
                this.quantity,
                this.unitPrice,
                this.amount,
                this.frequency,
                this.days,
                this.notes
        );
    }

    public static MedicationItemDto of(MedicationItem item) {
        return new MedicationItemDto().builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .frequency(item.getFrequency())
                .days(item.getDays())
                .notes(item.getNotes())
                .build();
    }
}
