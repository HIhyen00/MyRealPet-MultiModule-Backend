package petlifecycle.client.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petlifecycle.dto.medical.entity.TestItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestItemDto {
    private Long id;
    private String name;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
    private String notes;

    public TestItem toTestItem(Long medicalRecordId) {
        return new TestItem(
                medicalRecordId,
                this.name,
                this.quantity,
                this.unitPrice,
                this.amount,
                this.notes
        );
    }
}
