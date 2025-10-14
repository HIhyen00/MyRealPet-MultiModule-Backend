package petlifecycle.core.healthnote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petlifecycle.dto.healthnote.entity.HealthNote;

import java.util.List;

@Repository
public interface HealthNoteRepository extends JpaRepository<HealthNote, Long> {

    List<HealthNote> findAllByPetIdOrderByRecordDateDesc(Long petId);
}
