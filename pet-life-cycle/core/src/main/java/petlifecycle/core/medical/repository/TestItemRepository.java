package petlifecycle.core.medical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petlifecycle.dto.medical.entity.TestItem;

import java.util.List;
import java.util.Optional;

public interface TestItemRepository extends JpaRepository<TestItem, Long> {
    List<TestItem> findByMedicalRecordIdAndIsDeletedFalse(Long recordId);
    Optional<TestItem> findByIdAndIsDeletedFalse(Long id);
}
