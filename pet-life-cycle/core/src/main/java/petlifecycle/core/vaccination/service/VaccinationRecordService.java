package petlifecycle.core.vaccination.service;

import petlifecycle.client.vaccination.request.RegisterVacRecordRequest;
import petlifecycle.client.vaccination.request.UpdateVacRecordRequest;
import petlifecycle.client.vaccination.response.ListVacRecordResponse;
import petlifecycle.client.vaccination.response.ReadVacRecordResponse;
import petlifecycle.client.vaccination.response.VaccinationRecordDto;
import petlifecycle.client.vaccination.response.VaccineWithRecordDto;
import petlifecycle.core.breed.repository.BreedRepository;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.core.vaccination.repository.VaccinationRecordRepository;
import petlifecycle.core.vaccine.repository.VaccineRepository;
import petlifecycle.dto.breed.entity.Breed;
import petlifecycle.dto.pet.entity.PetAccount;
import petlifecycle.dto.vaccination.entity.VaccinationRecord;
import petlifecycle.dto.vaccine.entity.Vaccine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VaccinationRecordService {

    private final PetAccountService petAccountService;
    private final BreedRepository breedRepository;
    private final VaccineRepository vaccineRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;

    public void registerVacRecord(Long accountId, Long petId, RegisterVacRecordRequest request) {
        petAccountService.validateAndGetPetAccount(petId, accountId);

        validateVacConstraints(request.getVaccineId(), request.getCustomVaccineName());

        VaccinationRecord vacRecord = request.toVaccinationRecord(petId);
        vaccinationRecordRepository.save(vacRecord);
    }

    public ReadVacRecordResponse readVacRecord(Long accountId, Long petId, Long recordId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        VaccinationRecord vacRecord = vaccinationRecordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new RuntimeException("접종 이력을 찾을 수 없습니다."));
        String vaccineName = null;
        if (vacRecord.getVaccineId() != null) {
            Vaccine vaccine = vaccineRepository.findById(vacRecord.getVaccineId())
                    .orElseThrow(() -> new RuntimeException("백신 정보를 찾는데 실패했습니다."));
            vaccineName = vaccine.getVaccineName();
        } else {
            vaccineName = vacRecord.getCustomVaccineName();
        }
        return ReadVacRecordResponse.from(vacRecord, vaccineName);
    }

    public ListVacRecordResponse listVacRecords(Long accountId, Long petId) {
        PetAccount petAccount = petAccountService.validateAndGetPetAccount(petId, accountId);
        Breed breed = breedRepository.findByIdAndIsDeletedFalse(petAccount.getMainBreedId())
                .orElse(null);

        List<Vaccine> vaccines = new ArrayList<>();
        if (breed != null) {
            vaccines = vaccineRepository.findBySpeciesAndIsDeletedFalseOrderByVaccineIdAsc(breed.getSpecies());
        }
        List<VaccinationRecord> vacRecs = vaccinationRecordRepository.findByPetIdAndIsDeletedFalseOrderByVaccinationDateDesc(petId);

        // 등록된 백신 접종 기록
        Map<Long, List<VaccinationRecord>> vacRecordMap = vacRecs.stream()
                .collect(Collectors.groupingBy(vacRec -> vacRec.getVaccineId() != null ? vacRec.getVaccineId() : -1L));

        List<VaccineWithRecordDto> vaccineWithRecordDtoList = vaccines.stream()
                .map(vaccine -> {
                    List<VaccinationRecord> recordList = vacRecordMap.getOrDefault(
                            vaccine.getVaccineId(),
                            Collections.emptyList()
                    );

                    List<VaccinationRecordDto> recordDtoList = recordList.stream()
                            .map(record -> VaccinationRecordDto.from(record))
                            .collect(Collectors.toList());

                    return VaccineWithRecordDto.from(vaccine, recordDtoList);
                })
                .collect(Collectors.toList());

        // 직접 입력한 백신 접종 기록
        List<VaccinationRecord> customVacRecordList = vacRecordMap.getOrDefault(-1L, Collections.emptyList());

        List<VaccinationRecordDto> customRecordDtoList = customVacRecordList.stream()
                .map(record -> VaccinationRecordDto.from(record))
                .collect(Collectors.toList());

        VaccineWithRecordDto customVaccineDto =  VaccineWithRecordDto.builder()
                .vaccineId(null)
                .vaccineName("기타")
                .description("등록되지 않은 백신 접종 내역")
                .vaccinationRecords(customRecordDtoList)
                .build();

        vaccineWithRecordDtoList.add(customVaccineDto);

        return new ListVacRecordResponse(vaccineWithRecordDtoList);
    }

    public void updateVaccinationRecord(Long accountId, Long petId, Long recordId, UpdateVacRecordRequest request) {
        petAccountService.validateAndGetPetAccount(petId, accountId);

        VaccinationRecord vacRecord = vaccinationRecordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new RuntimeException("접종 이력을 찾는데 실패했습니다."));

        validateVacConstraints(vacRecord.getVaccineId(), request.getCustomVaccineName());

        vacRecord.update(request.getCustomVaccineName(), request.getVaccinationDate(), request.getHospitalName());
    }

    public void deleteVacRecord(Long accountId, Long petId, Long recordId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);

        VaccinationRecord vacRecord = vaccinationRecordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new RuntimeException("접종 이력을 찾는데 실패했습니다."));

        vacRecord.delete();
    }

    private void validateVacConstraints(Long vaccineId, String customVaccineName) {
        boolean hasVac = (vaccineId != null && vaccineId != 0);
        boolean hascustomVac = (customVaccineName != null && !customVaccineName.trim().isEmpty());

        if (!hasVac && !hascustomVac) {
            throw new RuntimeException("접종 백신의 정보를 입력해주세요.");
        }
        if (hasVac && hascustomVac) {
            throw new RuntimeException("등록된 백신 정보 입력 시, 직접입력은 할 수 없습니다.");
        }

        if (hasVac) {
            Vaccine vaccine = vaccineRepository.findById(vaccineId)
                    .orElseThrow(() -> new RuntimeException("백신 정보를 찾는데 실패했습니다."));
        }
    }
}
