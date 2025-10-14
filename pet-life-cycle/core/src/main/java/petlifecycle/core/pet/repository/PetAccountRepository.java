package petlifecycle.core.pet.repository;

import org.springframework.stereotype.Repository;
import petlifecycle.dto.pet.entity.PetAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetAccountRepository extends JpaRepository<PetAccount, Long> {
    Optional<PetAccount> findByPetIdAndIsDeletedFalse(Long petId);
    List<PetAccount> findByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(Long accountId);
}
