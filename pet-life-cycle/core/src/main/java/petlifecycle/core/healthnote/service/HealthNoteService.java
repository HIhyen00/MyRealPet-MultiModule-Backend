package petlifecycle.core.healthnote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petlifecycle.client.healthnote.request.HealthNoteCreateRequest;
import petlifecycle.client.healthnote.response.HealthNoteResponse;
import petlifecycle.core.healthnote.repository.HealthNoteRepository;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.dto.healthnote.entity.HealthNote;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthNoteService {

    private final HealthNoteRepository healthNoteRepository;
    private final PetAccountService petAccountService;

    @Transactional
    public void saveHealthNote(Long petId, HealthNoteCreateRequest request, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        LocalDate recordDate = request.getRecordDate() != null ? request.getRecordDate() : LocalDate.now();
        HealthNote healthNote = HealthNote.create(
                petId,
                recordDate,
                request.getMood(),
                request.getPoop(),
                request.getPee(),
                request.getSymptoms()
        );
        healthNoteRepository.save(healthNote);
    }

    public List<HealthNoteResponse> findHealthNotes(Long petId, Long accountId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        return healthNoteRepository.findAllByPetIdOrderByRecordDateDesc(petId).stream()
                .map(HealthNoteResponse::from)
                .collect(Collectors.toList());
    }
}
