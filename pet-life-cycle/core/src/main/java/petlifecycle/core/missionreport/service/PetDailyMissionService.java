package petlifecycle.core.missionreport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petlifecycle.client.missionreport.request.CreateDailyMissionRequest;
import petlifecycle.client.missionreport.response.DailyMissionResponse;
import petlifecycle.core.missionreport.repository.PetDailyMissionRepository;
import petlifecycle.core.pet.repository.PetAccountRepository;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.dto.missionreport.entity.PetDailyMission;
import petlifecycle.dto.pet.entity.PetAccount;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetDailyMissionService {

    private final PetAccountService petAccountService;
    private final PetDailyMissionRepository petDailyMissionRepository;
    private final PetAccountRepository petAccountRepository;

    public DailyMissionResponse createDailyMission(Long petId, CreateDailyMissionRequest request) {
        PetAccount petAccount = petAccountRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pet ID: " + petId));

        PetDailyMission petDailyMission = new PetDailyMission(petAccount, request.getMissionContent());
        petDailyMissionRepository.save(petDailyMission);

        return DailyMissionResponse.from(petDailyMission);
    }

    public List<DailyMissionResponse> getDailyMissions(Long petId) {
        PetAccount petAccount = petAccountRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pet ID: " + petId));

        List<PetDailyMission> petDailyMissions = petDailyMissionRepository.findByPetAccountAndDate(petAccount, LocalDate.now());

        return petDailyMissions.stream()
                .map(DailyMissionResponse::from)
                .collect(Collectors.toList());
    }

    public void completeDailyMission(Long missionId) {
        PetDailyMission petDailyMission = petDailyMissionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mission ID: " + missionId));

        petDailyMission.complete();
        petDailyMissionRepository.save(petDailyMission);
    }
}
