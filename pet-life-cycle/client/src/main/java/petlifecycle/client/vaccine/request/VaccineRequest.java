package petlifecycle.client.vaccine.request;

import petlifecycle.dto.breed.entity.Species;
import petlifecycle.dto.vaccine.entity.Vaccine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VaccineRequest {
    private String species;
    private String vaccineName;
    private String description;
    private String sideEffects;
    private Integer vaccineCycle;

    public Vaccine toEntity() {
        return Vaccine.builder()
                .species(Species.valueOf(this.species))
                .vaccineName(this.vaccineName)
                .description(this.description)
                .sideEffects(this.sideEffects)
                .vaccineCycle(this.vaccineCycle)
                .build();
    }
}
