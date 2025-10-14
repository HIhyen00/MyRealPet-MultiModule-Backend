package petlifecycle.client.breed.response;

import petlifecycle.dto.breed.entity.Breed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ListBreedResponse {
    private final List<Map<String, Object>> breedList;
    private final int currentPage;
    private final int totalPages;
    private final long totalItems;

    public List<Map<String, Object>> toBreedList() {
        return breedList;
    }

    public static ListBreedResponse from(List<Breed> tobreedList, int currentPage, int totalPages, long totalItems) {
        List<Map<String, Object>> breedList = tobreedList.stream()
                .map(breed -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", breed.getId());
                    map.put("species", breed.getSpecies());
                    map.put("name", breed.getName());
                    return map;
                })
                .collect(Collectors.toList());
        return new ListBreedResponse(breedList, currentPage, totalPages, totalItems);
    }
}
