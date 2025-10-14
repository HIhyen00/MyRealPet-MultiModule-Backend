package petlifecycle.client.vaccination.response;

import petlifecycle.dto.vaccination.entity.VaccinationRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ReadVacRecordResponse {
    private final Long vaccineId;
    private final String vaccineName;
    private final String customVaccineName;
    private final LocalDate vaccinationDate;
    private final String hospitalName;

    public static ReadVacRecordResponse from(VaccinationRecord vacRecord, String vaccineName) {
        return ReadVacRecordResponse.builder()
                .vaccineId(vacRecord.getVaccineId())
                .vaccineName(vaccineName)
                .customVaccineName(vacRecord.getCustomVaccineName())
                .vaccinationDate(vacRecord.getVaccinationDate())
                .hospitalName(vacRecord.getHospitalName())
                .build();
    }
}
