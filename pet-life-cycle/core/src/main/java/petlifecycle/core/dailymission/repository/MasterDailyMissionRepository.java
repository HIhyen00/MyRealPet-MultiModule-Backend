package petlifecycle.core.dailymission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petlifecycle.dto.dailymission.entity.DailyMission;

@Repository
public interface MasterDailyMissionRepository extends JpaRepository<DailyMission, Long> {
}
