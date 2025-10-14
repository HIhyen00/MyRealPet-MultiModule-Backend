package petlifecycle.client.breed.request;

import petlifecycle.dto.breed.entity.Species;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateBreedRequest {
    private final String name;
    private final Species species;
    private final String description;
}
