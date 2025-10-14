package petlifecycle.core.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petlifecycle.dto.medical.entity.TreatmentItem;

import java.util.List;
import java.util.Optional;

public interface TreatmentItemRepository extends JpaRepository<TreatmentItem, Long> {
    List<TreatmentItem> findByMedicalRecordIdAndIsDeletedFalse(Long recordId);
    Optional<TreatmentItem> findByIdAndIsDeletedFalse(Long id);
}
