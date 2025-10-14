package petlifecycle.dto.vaccination.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long petId;

    private Long vaccineId;

    private String customVaccineName;

    @Column(nullable = false)
    private LocalDate vaccinationDate;

    private String hospitalName;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted=false;

    public VaccinationRecord(Long petId, Long vaccineId, String customVaccineName, LocalDate vaccinationDate, String hospitalName) {
        this.petId = petId;
        this.vaccineId = vaccineId;
        this.customVaccineName = customVaccineName;
        this.vaccinationDate = vaccinationDate;
        this.hospitalName = hospitalName;
    }

    // 백신을 변경할 수는 없음
    public void update(String customVaccineName, LocalDate vaccinationDate, String hospitalName) {
        this.customVaccineName = customVaccineName;
        this.vaccinationDate = vaccinationDate;
        this.hospitalName = hospitalName;
    }

    public void delete() {
        isDeleted = true;
    }
}
