package petlifecycle.client.vaccination.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class UpdateVacRecordRequest {

    private final String customVaccineName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate vaccinationDate;
    private final String hospitalName;
}
