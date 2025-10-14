package petlifecycle.client.cycle.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PetCycleCreateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String memo;
}
