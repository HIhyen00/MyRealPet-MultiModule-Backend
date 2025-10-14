package petlifecycle.core.cycle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petlifecycle.dto.cycle.entity.PetCycle;

import java.util.List;

@Repository
public interface PetCycleRepository extends JpaRepository<PetCycle, Long> {

    List<PetCycle> findAllByPetId(Long petId);
}
