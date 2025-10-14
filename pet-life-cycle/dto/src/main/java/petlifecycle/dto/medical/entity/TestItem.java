package petlifecycle.dto.medical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class TestItem {
    // 검사내역
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
    private String notes;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public TestItem(Long medicalRecordId, String name, Integer quantity,
                    Integer unitPrice, Integer amount, String notes) {
        this.medicalRecordId = medicalRecordId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.notes = notes;
    }

    public void update(String name, Integer quantity, Integer unitPrice, Integer amount, String notes) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.notes = notes;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
