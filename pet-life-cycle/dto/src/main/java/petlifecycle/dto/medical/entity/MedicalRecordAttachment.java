package petlifecycle.dto.medical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class MedicalRecordAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long medicalRecordId;

    // MetaDataFile의 ID
    @Column(nullable = false)
    private Long fileId;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public MedicalRecordAttachment(Long medicalRecordId, Long fileId) {
        this.medicalRecordId = medicalRecordId;
        this.fileId = fileId;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
