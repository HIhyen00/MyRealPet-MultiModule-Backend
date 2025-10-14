package petlifecycle.core.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petlifecycle.dto.medical.entity.MedicalRecordAttachment;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordAttachmentRepository extends JpaRepository<MedicalRecordAttachment, Long> {
    List<MedicalRecordAttachment> findByMedicalRecordIdAndIsDeletedFalse(Long recordId);
    Optional<MedicalRecordAttachment> findByIdAndIsDeletedFalse(Long id);
}
