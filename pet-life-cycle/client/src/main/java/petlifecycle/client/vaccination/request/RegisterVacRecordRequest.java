package petlifecycle.client.vaccination.request;

import petlifecycle.dto.vaccination.entity.VaccinationRecord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class RegisterVacRecordRequest {
    private final Long vaccineId;
    private final String customVaccineName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate vaccinationDate;
    private final String hospitalName;

    public VaccinationRecord toVaccinationRecord(Long petId) {
        return new VaccinationRecord(petId, vaccineId, customVaccineName, vaccinationDate, hospitalName);
    }
}
