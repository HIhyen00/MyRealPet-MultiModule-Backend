package petlifecycle.dto.medical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class MedicationItem {
    // 처방내역
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long medicalRecordId;

    @Column(nullable = false)
    private String name;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
    private String frequency;
    private Integer days;
    private String notes;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public MedicationItem(Long medicalRecordId, String name, Integer quantity, Integer unitPrice,
                          Integer amount, String frequency, Integer days, String notes) {
        this.medicalRecordId = medicalRecordId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.frequency = frequency;
        this.days = days;
        this.notes = notes;
    }

    public void update(String name, Integer quantity, Integer unitPrice, Integer amount, String frequency, Integer days, String notes) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.frequency = frequency;
        this.days = days;
        this.notes = notes;
    }

    public void delete() {
        this.isDeleted = true;
    }

}
