package petlifecycle.client.breed.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ListBreedRequest {
    private int page = 0;
    private int perPage = 100;
}
