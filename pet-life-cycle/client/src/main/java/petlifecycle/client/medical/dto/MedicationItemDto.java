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
    private String notes;

    public MedicationItem toMedicationItem(Long medicalRecordId) {
        return new MedicationItem(
                medicalRecordId,
                this.name,
                this.quantity,
                this.unitPrice,
                this.amount,
                this.notes
        );
    }
}
