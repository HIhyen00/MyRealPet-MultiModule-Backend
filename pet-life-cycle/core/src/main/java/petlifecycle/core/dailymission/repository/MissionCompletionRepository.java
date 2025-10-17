package petlifecycle.core.dailymission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petlifecycle.dto.dailymission.entity.MissionCompletion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionCompletionRepository extends JpaRepository<MissionCompletion, Long> {

    // 월별 기록 조회를 위한 쿼리
    List<MissionCompletion> findAllByUserIdAndCompletedDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // 특정 날짜에 특정 미션을 완료했는지 확인하기 위한 쿼리
    Optional<MissionCompletion> findByUserIdAndDailyMissionIdAndCompletedDate(Long userId, Long dailyMissionId, LocalDate completedDate);

    // 특정 유저의 전체 미션 완료 횟수 조회를 위한 쿼리
    long countByUserId(Long userId);
}