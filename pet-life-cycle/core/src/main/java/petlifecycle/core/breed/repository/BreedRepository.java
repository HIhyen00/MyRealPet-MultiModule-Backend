package petlifecycle.core.breed.repository;

import petlifecycle.dto.breed.entity.Breed;
import petlifecycle.dto.breed.entity.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
    boolean existsByIdAndIsDeletedFalse(Long id);
    Optional<Breed> findByIdAndIsDeletedFalse(Long id);
    Page<Breed> findAllByIsDeletedFalse(Pageable pageable);
    Page<Breed> findAllBySpeciesAndIsDeletedFalse(Species species, Pageable pageable);
}
