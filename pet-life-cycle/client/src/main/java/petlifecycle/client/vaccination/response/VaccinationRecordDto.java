package petlifecycle.client.vaccination.response;

import petlifecycle.dto.vaccination.entity.VaccinationRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class VaccinationRecordDto {
    private final Long recordId;
    private final String customVaccineName;
    private final LocalDate vaccinationDate;
    private final String hospitalName;

    public static VaccinationRecordDto from(VaccinationRecord vacRecord) {
        return VaccinationRecordDto.builder()
                .recordId(vacRecord.getId())
                .customVaccineName(vacRecord.getCustomVaccineName() != null ? vacRecord.getCustomVaccineName() : null)
                .vaccinationDate(vacRecord.getVaccinationDate())
                .hospitalName(vacRecord.getHospitalName())
                .build();
    }
}
