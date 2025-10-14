package petlifecycle.dto.healthreport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import petlifecycle.dto.pet.entity.PetAccount;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class HealthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private PetAccount petAccount;

    @Lob
    private String reportContent;

    private LocalDateTime createdAt;

    public HealthReport(PetAccount petAccount, String reportContent) {
        this.petAccount = petAccount;
        this.reportContent = reportContent;
        this.createdAt = LocalDateTime.now();
    }
}
