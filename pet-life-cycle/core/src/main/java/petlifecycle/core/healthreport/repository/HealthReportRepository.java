package petlifecycle.core.healthreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import petlifecycle.dto.healthreport.entity.HealthReport;

public interface HealthReportRepository extends JpaRepository<HealthReport, Long> {
}
