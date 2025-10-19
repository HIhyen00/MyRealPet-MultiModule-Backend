package petlifecycle.api.healthreport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petlifecycle.client.healthreport.request.AiQuestionRequest;
import petlifecycle.client.healthreport.request.CreateHealthReportRequest;
import petlifecycle.client.healthreport.response.AiResponse;
import petlifecycle.client.healthreport.response.HealthReportResponse;
import petlifecycle.core.healthreport.service.HealthReportService;

@RestController
@RequestMapping("/api/pets/{petId}/health-reports")
@RequiredArgsConstructor
public class HealthReportController {

    private final HealthReportService healthReportService;

    @PostMapping
    public ResponseEntity<HealthReportResponse> createHealthReport(
            @PathVariable Long petId,
            @RequestBody CreateHealthReportRequest request,
            @RequestAttribute("userId") Long userId) {
        HealthReportResponse response = healthReportService.createHealthReport(petId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reportId}/ask-ai")
    public ResponseEntity<AiResponse> getAiAnswer(
            @PathVariable Long petId,
            @PathVariable String reportId,
            @RequestBody AiQuestionRequest request,
            @RequestAttribute("userId") Long userId) {
        Long parsedReportId;
        try {
            String numericPart = reportId.substring(reportId.lastIndexOf('-') + 1);
            parsedReportId = Long.parseLong(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid report ID format: " + reportId);
        }

        AiResponse response = healthReportService   .getAnswer(parsedReportId, request.getQuestion(), petId, userId);
        return ResponseEntity.ok(response);
    }
}
