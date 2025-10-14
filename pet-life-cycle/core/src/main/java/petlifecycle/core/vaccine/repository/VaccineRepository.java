package petlifecycle.core.vaccine.repository;

import org.springframework.stereotype.Repository;
import petlifecycle.dto.breed.entity.Species;
import petlifecycle.dto.vaccine.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

    List<Vaccine> findBySpeciesAndIsDeletedFalseOrderByVaccineIdAsc(Species s);
    Optional<Vaccine> findById(Long vaccineId);
}
