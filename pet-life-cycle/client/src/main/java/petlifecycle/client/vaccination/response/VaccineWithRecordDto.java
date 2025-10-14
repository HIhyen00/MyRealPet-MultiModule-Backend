package petlifecycle.client.vaccination.response;

import petlifecycle.dto.vaccine.entity.Vaccine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class VaccineWithRecordDto {
    private final Long vaccineId;
    private final String vaccineName;
    private final String description;
    private final List<VaccinationRecordDto> vaccinationRecords;

    public static VaccineWithRecordDto from(Vaccine vaccine, List<VaccinationRecordDto> vaccinationRecords) {
        return VaccineWithRecordDto.builder()
                .vaccineId(vaccine.getVaccineId())
                .vaccineName(vaccine.getVaccineName())
                .description(vaccine.getDescription())
                .vaccinationRecords(vaccinationRecords)
                .build();
    }
}
