package petlifecycle.core.weight.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petlifecycle.client.weight.request.PetWeightCreateRequest;
import petlifecycle.client.weight.response.PetWeightResponse;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.core.weight.repository.PetWeightRepository;
import petlifecycle.dto.weight.entity.PetWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetWeightService {

    private final PetWeightRepository petWeightRepository;
    private final PetAccountService petAccountService;

    @Transactional
    public void saveWeight(Long petId, PetWeightCreateRequest request, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        // 요청 날짜가 없으면 오늘 날짜로 기록
        LocalDate recordDate = request.getRecordDate() != null ? request.getRecordDate() : LocalDate.now();
        PetWeight petWeight = PetWeight.create(petId, request.getWeight(), recordDate);
        petWeightRepository.save(petWeight);
    }

    public List<PetWeightResponse> findWeights(Long petId, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        return petWeightRepository.findAllByPetIdOrderByRecordDateAsc(petId).stream()
                .map(PetWeightResponse::from)
                .collect(Collectors.toList());
    }
}
