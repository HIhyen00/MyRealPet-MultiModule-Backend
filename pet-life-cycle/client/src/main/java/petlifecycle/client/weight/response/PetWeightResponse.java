package petlifecycle.client.weight.response;

import lombok.Getter;
import petlifecycle.dto.weight.entity.PetWeight;

import java.time.LocalDate;

@Getter
public class PetWeightResponse {
    private Long id;
    private Double weight;
    private LocalDate recordDate;

    private PetWeightResponse(Long id, Double weight, LocalDate recordDate) {
        this.id = id;
        this.weight = weight;
        this.recordDate = recordDate;
    }

    public static PetWeightResponse from(PetWeight petWeight) {
        return new PetWeightResponse(
                petWeight.getId(),
                petWeight.getWeight(),
                petWeight.getRecordDate()
        );
    }
}
