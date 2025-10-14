package petlifecycle.client.medical.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ListMedicalRecordRequest {
    private int page = 0;
    private int perPage = 100;
}
