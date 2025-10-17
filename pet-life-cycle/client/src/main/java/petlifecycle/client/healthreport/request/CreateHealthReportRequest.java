package petlifecycle.client.healthreport.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateHealthReportRequest {
    private SurveyResultDto surveyResult;  // String -> SurveyResultDto로 변경!

    @Getter
    @NoArgsConstructor
    public static class SurveyResultDto {
        private Map<String, Map<String, String>> answers;
        private List<QuestionDto> questions;
        private Map<String, Integer> scores;
        private Integer overallScore;
    }

    @Getter
    @NoArgsConstructor
    public static class QuestionDto {
        private String category;
        private String subKey;
        private String text;
        private String answer;
    }
}
