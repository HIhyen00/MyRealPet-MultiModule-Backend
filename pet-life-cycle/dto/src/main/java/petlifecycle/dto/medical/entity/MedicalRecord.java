package petlifecycle.dto.medical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import petlifecycle.dto.metadata.entity.MetaDataFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long petId;

    @Column(nullable = false)
    private Long accountId;

    // 행정 정보
    private String hospitalName;
    private String hospitalNumber;
    private String hospitalAddress;

    @Column(nullable = false)
    private LocalDate visitDate;

    private Integer totalAmount;
    private Integer vatAmount;

    @Column(length = 500)
    private String diagnosis;

    @Column(length = 2000)
    private String symptoms;

    // 청구서 파일 ID (MetaDataFile)
    private Long receiptFiledId;

    // @Transient
    @Transient
    private List<TestItem> testItems = new ArrayList<>();

    @Transient
    private List<TreatmentItem> treatmentItems = new ArrayList<>();

    @Transient
    private List<MedicationItem> medicationItems = new ArrayList<>();

    @Transient
    private List<MetaDataFile> attachmentFiles = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public MedicalRecord(Long petId, Long accountId, String hospitalName, String hospitalNumber, String hospitalAddress,
                         LocalDate visitDate, Integer totalAmount, Integer vatAmount, Long receiptFiledId, String diagnosis, String symptoms) {
        this.petId = petId;
        this.accountId = accountId;
        this.hospitalName = hospitalName;
        this.hospitalNumber = hospitalNumber;
        this.hospitalAddress = hospitalAddress;
        this.visitDate = visitDate;
        this.totalAmount = totalAmount;
        this.vatAmount = vatAmount;
        this.receiptFiledId = receiptFiledId;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
    }

    public void update(String hospitalName, String hospitalNumber, String hospitalAddress,
                       LocalDate visitDate, Integer totalAmount, Integer vatAmount,
                       Long receiptFiledId, String diagnosis, String symptoms) {
        this.hospitalName = hospitalName;
        this.hospitalNumber = hospitalNumber;
        this.hospitalAddress = hospitalAddress;
        this.visitDate = visitDate;
        this.totalAmount = totalAmount;
        this.vatAmount = vatAmount;
        this.receiptFiledId = receiptFiledId;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
    }

    public void delete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }
}
