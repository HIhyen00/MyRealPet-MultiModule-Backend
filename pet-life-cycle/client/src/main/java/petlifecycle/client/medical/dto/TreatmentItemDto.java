package petlifecycle.client.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petlifecycle.dto.medical.entity.TreatmentItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentItemDto {
    private Long id;
    private String name;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
    private String notes;

    public TreatmentItem toTreatmentItem(Long medicalRecordId) {
        return new TreatmentItem(
                medicalRecordId,
                this.name,
                this.quantity,
                this.unitPrice,
                this.amount,
                this.notes
        );
    }

    public static TreatmentItemDto of(TreatmentItem item) {
        return new TreatmentItemDto().builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .notes(item.getNotes())
                .build();
    }
}
