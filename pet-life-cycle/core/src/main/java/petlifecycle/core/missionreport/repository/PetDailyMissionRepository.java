package petlifecycle.core.missionreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petlifecycle.dto.missionreport.entity.PetDailyMission;
import petlifecycle.dto.pet.entity.PetAccount;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PetDailyMissionRepository extends JpaRepository<PetDailyMission, Long> {
    List<PetDailyMission> findByPetAccountAndDate(PetAccount petAccount, LocalDate date);
}
