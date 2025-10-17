package petlifecycle.core.healthreport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import petlifecycle.client.healthreport.request.CreateHealthReportRequest;
import petlifecycle.client.healthreport.response.AiResponse;
import petlifecycle.client.healthreport.response.HealthReportResponse;
import petlifecycle.core.ai.service.OpenAIService;
import petlifecycle.core.healthreport.repository.HealthReportRepository;
import petlifecycle.core.pet.repository.PetAccountRepository;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.dto.healthreport.entity.HealthReport;
import petlifecycle.dto.pet.entity.PetAccount;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthReportService {

    private final ObjectMapper objectMapper;
    private final OpenAIService openAIService;
    private final HealthReportRepository healthReportRepository;
    private final PetAccountRepository petAccountRepository;
    private final PetAccountService petAccountService;

    public HealthReportResponse createHealthReport(Long petId, CreateHealthReportRequest request, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        PetAccount petAccount = petAccountRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pet ID: " + petId));

        String surveyResultJson = convertToJson(request.getSurveyResult());
        String prompt = createHealthReportPrompt(petAccount, surveyResultJson);
        String reportContent = openAIService.getChatResponse(prompt).block();

        HealthReport healthReport = new HealthReport(petAccount, reportContent);
        healthReportRepository.save(healthReport);
        log.info("Health Report saved: id={}, reportContent={}",
                healthReport.getId(),
                reportContent);
        HealthReportResponse response = HealthReportResponse.from(healthReport);
        log.info("Response created: {}", response);

        return response;
    }

    public AiResponse getAnswer(Long reportId, String question) {
        // reportId를 사용할 수 있지만, 지금은 질문 내용만으로 답변을 생성합니다.
        String answer;
        if (question.contains("영양소") || question.contains("부족")) {
            answer = "리포트 결과에 따르면, 현재 반려동물은 비타민 D와 오메가-3가 부족할 수 있습니다. 수의사와 상담하여 영양제 급여를 고려해보세요.";
        } else if (question.contains("활동") || question.contains("운동")) {
            answer = "현재 체중을 고려할 때, 하루 30분 이상의 산책과 주 2회 이상의 인터랙티브 놀이(예: 공 던지기, 낚싯대 놀이)를 추천합니다.";
        } else if (question.contains("비만") || question.contains("체중")) {
            answer = "현재 체중은 정상 범위에서 약간 높은 편입니다. 꾸준한 활동과 함께 사료 양을 10% 정도 줄여보는 것을 권장합니다. 정확한 진단은 수의사와 상의하세요.";
        } else {
            answer = "죄송합니다. 질문을 이해하지 못했어요. 리포트와 관련된 다른 질문을 해주시겠어요?";
        }

        return new AiResponse(answer);
    }

    private String createHealthReportPrompt(PetAccount petAccount, String surveyResult) {
        // Here you can create a more detailed prompt based on the pet's information and the survey result.
        return "Create a health report for my pet, " + petAccount.getName() + ".\n" +
                "Here is the survey result:\n" + surveyResult;
    }

    private String convertToJson(CreateHealthReportRequest.SurveyResultDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert survey result to JSON", e);
        }
    }
}
