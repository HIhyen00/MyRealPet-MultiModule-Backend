package petlifecycle.core.breed.service;

import petlifecycle.client.breed.request.ListBreedRequest;
import petlifecycle.client.breed.request.RegisterBreedRequest;
import petlifecycle.client.breed.request.UpdateBreedRequest;
import petlifecycle.client.breed.response.ListBreedResponse;
import petlifecycle.client.breed.response.ReadBreedResponse;
import petlifecycle.client.breed.response.RegisterBreedResponse;
import petlifecycle.client.breed.response.UpdateBreedResponse;
import petlifecycle.core.breed.repository.BreedRepository;
import petlifecycle.dto.breed.entity.Breed;
import petlifecycle.dto.breed.entity.Species;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;

    public RegisterBreedResponse registerBreed(RegisterBreedRequest request) {
        Breed savedBreed = breedRepository.save(request.toBreed());
        return RegisterBreedResponse.from(savedBreed);
    }

    public ListBreedResponse getAllBreed(ListBreedRequest request) {
        int page = request.getPage() > 0 ? request.getPage() - 1 : 0;  // 0-based page index
        int perPage = request.getPerPage() > 0 ? request.getPerPage() : 10;

        Pageable pageable = PageRequest.of(page, perPage);
        Page<Breed> paginatedBreedList = breedRepository.findAllByIsDeletedFalse(pageable);

        List<Breed> breedList = paginatedBreedList.getContent();

        return ListBreedResponse.from(
                breedList,
                paginatedBreedList.getNumber() + 1,
                paginatedBreedList.getTotalPages(),
                paginatedBreedList.getTotalElements()
        );
    }

    public ReadBreedResponse readBreed(Long breedId) {
        Breed foundBreed = breedRepository.findByIdAndIsDeletedFalse(breedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 Breed입니다. ID: " + breedId));
        return ReadBreedResponse.from(foundBreed);
    }

    public UpdateBreedResponse updateBreed(Long breedId, UpdateBreedRequest request) {
        Breed foundBreed = breedRepository.findByIdAndIsDeletedFalse(breedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 Breed입니다. ID: " + breedId));
        foundBreed.update(request.getName(), request.getDescription(), request.getSpecies());

        Breed updatedBreed = breedRepository.save(foundBreed);

        return UpdateBreedResponse.from(updatedBreed);
    }

    public void deleteBreed(Long breedId) {
        Breed foundBreed = breedRepository.findByIdAndIsDeletedFalse(breedId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 Breed입니다. ID: " + breedId));
        foundBreed.delete();

        Breed deletedBreed = breedRepository.save(foundBreed);
    }

    public ListBreedResponse getBreedBySpecies(Species species, ListBreedRequest request) {
        int page = request.getPage() > 0 ? request.getPage() - 1 : 0;  // 0-based page index
        int perPage = request.getPerPage() > 0 ? request.getPerPage() : 10;

        Pageable pageable = PageRequest.of(page, perPage);
        Page<Breed> paginatedlaptopList = breedRepository.findAllBySpeciesAndIsDeletedFalse(species, pageable);

        List<Breed> breedList = paginatedlaptopList.getContent();

        return ListBreedResponse.from(
                breedList,
                paginatedlaptopList.getNumber() + 1,
                paginatedlaptopList.getTotalPages(),
                paginatedlaptopList.getTotalElements()
        );
    }
}
