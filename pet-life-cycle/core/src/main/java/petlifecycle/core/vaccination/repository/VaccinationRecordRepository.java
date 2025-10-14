package petlifecycle.core.vaccination.repository;

import org.springframework.stereotype.Repository;
import petlifecycle.dto.vaccination.entity.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    Optional<VaccinationRecord> findByIdAndIsDeletedFalse(Long id);
    List<VaccinationRecord> findByPetIdAndIsDeletedFalseOrderByVaccinationDateDesc(Long petId);
}
