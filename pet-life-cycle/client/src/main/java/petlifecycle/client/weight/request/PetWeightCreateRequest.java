package petlifecycle.client.weight.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PetWeightCreateRequest {
    private Double weight;
    private LocalDate recordDate;
}
