package petlifecycle.core.weight.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petlifecycle.dto.weight.entity.PetWeight;

import java.util.List;

@Repository
public interface PetWeightRepository extends JpaRepository<PetWeight, Long> {

    List<PetWeight> findAllByPetIdOrderByRecordDateAsc(Long petId);
}
