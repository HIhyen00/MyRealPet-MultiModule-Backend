package petlifecycle.core.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petlifecycle.dto.medical.entity.MedicationItem;

import java.util.Optional;
import java.util.List;

public interface MedicationItemRepository extends JpaRepository<MedicationItem, Long> {
    List<MedicationItem> findByMedicalRecordIdAndIsDeletedFalse(Long recordId);
    Optional<MedicationItem> findByIdAndIsDeletedFalse(Long id);
}
