package petlifecycle.client.healthreport.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateHealthReportRequest {
    private String surveyResult;
}
