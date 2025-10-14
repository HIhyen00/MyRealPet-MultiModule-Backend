package petlifecycle.client.vaccine.response;

import petlifecycle.dto.vaccine.entity.Vaccine;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VaccineResponse {
    private final Long vaccineId;
    private final String species;
    private final String vaccineName;
    private final String description;
    private final String sideEffects;
    private final Integer vaccineCycle;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public VaccineResponse(Vaccine vaccine) {
        this.vaccineId = vaccine.getVaccineId();
        this.species = vaccine.getSpecies().name();
        this.vaccineName = vaccine.getVaccineName();
        this.description = vaccine.getDescription();
        this.sideEffects = vaccine.getSideEffects();
        this.vaccineCycle = vaccine.getVaccineCycle();
        this.createdAt = vaccine.getCreatedAt();
        this.updatedAt = vaccine.getUpdatedAt();

    }

    public static VaccineResponse fromVaccine(Vaccine vaccine) {
        return new VaccineResponse(vaccine);
    }
}
