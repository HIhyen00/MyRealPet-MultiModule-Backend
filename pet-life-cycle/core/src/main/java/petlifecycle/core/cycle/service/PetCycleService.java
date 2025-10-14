package petlifecycle.core.cycle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petlifecycle.client.cycle.request.PetCycleCreateRequest;
import petlifecycle.client.cycle.response.PetCycleResponse;
import petlifecycle.core.cycle.repository.PetCycleRepository;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.dto.cycle.entity.PetCycle;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetCycleService {

    private final PetCycleRepository petCycleRepository;
    private final PetAccountService petAccountService;

    @Transactional
    public void saveCycle(Long petId, PetCycleCreateRequest request, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        PetCycle petCycle = PetCycle.create(petId,
                request.getStartDate(),
                request.getEndDate(),
                request.getMemo());
        petCycleRepository.save(petCycle);
    }

    public List<PetCycleResponse> findCycles(Long petId, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        return petCycleRepository.findAllByPetId(petId).stream()
                .map(PetCycleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCycle(Long cycleId, Long petId, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        if (!petCycleRepository.existsById(cycleId)) {
            throw new IllegalArgumentException("존재하지 않는 기록입니다.");
        }
        petCycleRepository.deleteById(cycleId);
    }
}
