package petlifecycle.client.healthreport.response;

import lombok.Getter;
import petlifecycle.dto.healthreport.entity.HealthReport;

import java.time.LocalDateTime;

@Getter
public class HealthReportResponse {
    private Long id;
    private String reportContent;
    private LocalDateTime createdAt;

    public HealthReportResponse(Long id, String reportContent, LocalDateTime createdAt) {
        this.id = id;
        this.reportContent = reportContent;
        this.createdAt = createdAt;
    }

    public static HealthReportResponse from(HealthReport healthReport) {
        return new HealthReportResponse(
                healthReport.getId(),
                healthReport.getReportContent(),
                healthReport.getCreatedAt()
        );
    }
}
