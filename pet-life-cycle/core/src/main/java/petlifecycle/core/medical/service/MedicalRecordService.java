package petlifecycle.core.medical.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petlifecycle.client.medical.dto.FileInfoDto;
import petlifecycle.client.medical.dto.MedicationItemDto;
import petlifecycle.client.medical.dto.TestItemDto;
import petlifecycle.client.medical.dto.TreatmentItemDto;
import petlifecycle.client.medical.request.RegisterMedicalRecordRequest;
import petlifecycle.client.medical.request.UpdateMedicalRecordRequest;
import petlifecycle.client.medical.response.ListMedicalRecordResponse;
import petlifecycle.client.medical.response.ReadMedicalRecordResponse;
import petlifecycle.core.medical.repository.*;
import petlifecycle.core.metadata.repository.MetaDataFileRepository;
import petlifecycle.core.metadata.service.FileService;
import petlifecycle.core.pet.service.PetAccountService;
import petlifecycle.dto.medical.entity.*;
import petlifecycle.dto.metadata.entity.MetaDataFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {

    private final PetAccountService petAccountService;
    private final FileService fileService;
    private final MetaDataFileRepository metaDataFileRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final TestItemRepository testItemRepository;
    private final TreatmentItemRepository treatmentItemRepository;
    private final MedicationItemRepository medicationItemRepository;
    private final MedicalRecordAttachmentRepository medicalRecordAttachmentRepository;


    public void registerMedicalRecord(Long accountId, Long petId, RegisterMedicalRecordRequest request) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        MedicalRecord medicalRecord = medicalRecordRepository.save(request.toMedicalRecord(petId, accountId));
        Long medicalRecordId = medicalRecord.getId();

        if (request.getTestItems() != null) {
            request.getTestItems().stream()
                    .map(dto -> dto.toTestItem(medicalRecordId))
                    .forEach(testItemRepository::save);
        }

        // 처치내역
        if (request.getTreatmentItems() != null) {
            request.getTreatmentItems().stream()
                    .map(dto -> dto.toTreatmentItem(medicalRecordId))
                    .forEach(treatmentItemRepository::save);
        }

        // 처방내역
        if (request.getMedicationItems() != null) {
            request.getMedicationItems().stream()
                    .map(dto -> dto.toMedicationItem(medicalRecordId))
                    .forEach(medicationItemRepository::save);
        }

        // 첨부파일
        if (request.getAttachmentFileIds() != null) {
            request.getAttachmentFileIds().stream()
                    .map(fileId -> new MedicalRecordAttachment(medicalRecordId, fileId))
                    .forEach(medicalRecordAttachmentRepository::save);
        }

        log.info("진료기록 등록 완료: recordId={}, petId={}, accountId={}", medicalRecordId, petId, accountId);
    }

    public ListMedicalRecordResponse listMedicalRecord(Long accountId, Long petId, int page, int perPage) {
        petAccountService.validateAndGetPetAccount(petId, accountId);

        int pageIndex  = page > 0 ? page - 1 : 0;  // 0-based page index
        int size = perPage > 0 ? perPage : 10;

        Pageable pageable = PageRequest.of(pageIndex , size);
        Page<MedicalRecord> paginatedRecordList = medicalRecordRepository.findByPetIdAndIsDeletedFalseOrderByVisitDateDesc(petId, pageable);

        List<MedicalRecord> medicalRecordList = paginatedRecordList.getContent();

        return ListMedicalRecordResponse.from(
                medicalRecordList,
                paginatedRecordList.getNumber() + 1,
                paginatedRecordList.getTotalPages(),
                paginatedRecordList.getTotalElements()
        );
    }

    public ReadMedicalRecordResponse readMedicalRecord(Long accountId, Long petId, Long recordId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        MedicalRecord record = validateAndGetmedicalRecord(petId, recordId);

        // 검사 내역 조회
        List<TestItemDto> testItems = testItemRepository
                .findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .stream()
                .map(item -> TestItemDto.of(item))
                .collect(Collectors.toList());

        // 처치 내역 조회
        List<TreatmentItemDto> treatmentItems = treatmentItemRepository
                .findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .stream()
                .map(item -> TreatmentItemDto.of(item))
                .collect(Collectors.toList());

        // 처방 내역 조회
        List<MedicationItemDto> medicationItems = medicationItemRepository
                .findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .stream()
                .map(item -> MedicationItemDto.of(item))
                .collect(Collectors.toList());

        FileInfoDto receiptFile = null;
        if (record.getReceiptFiledId() != null) {
            receiptFile = getFileInfo(record.getReceiptFiledId());
        }

        // 첨부파일 정보 조회
        List<Long> attachmentFileIds = medicalRecordAttachmentRepository
                .findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .stream()
                .map(MedicalRecordAttachment::getFileId)
                .collect(Collectors.toList());

        List<FileInfoDto> attachmentFiles = attachmentFileIds.stream()
                .map(this::getFileInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ReadMedicalRecordResponse.of(record, receiptFile, attachmentFiles, testItems, treatmentItems, medicationItems);
    }

    public void updateMedicalRecord(Long accountId, Long petId, Long recordId, UpdateMedicalRecordRequest request) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        MedicalRecord record = validateAndGetmedicalRecord(petId, recordId);

        record.update(
                request.getHospitalName(), request.getHospitalNumber(), request.getHospitalAddress(),
                request.getVisitDate(), request.getTotalAmount(), request.getVatAmount(),
                request.getReceiptFileId(), request.getDiagnosis(), request.getSymptoms()
        );
        medicalRecordRepository.save(record);

        updateTestItems(recordId, request.getTestItems());
        updateTreatmentItems(recordId, request.getTreatmentItems());
        updateMedicationItems(recordId, request.getMedicationItems());
        updateAttachments(recordId, request.getAttachmentFileIds());

        log.info("진료기록 수정 완료: recordId={}", recordId);
    }

    public void deleteMedicalRecord(Long accountId, Long petId, Long recordId) {
        petAccountService.validateAndGetPetAccount(petId, accountId);
        MedicalRecord record = validateAndGetmedicalRecord(petId, recordId);

        testItemRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .forEach(item -> {
                    item.delete();
                    testItemRepository.save(item);
                });

        treatmentItemRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .forEach(item -> {
                    item.delete();
                    treatmentItemRepository.save(item);
                });

        medicationItemRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .forEach(item -> {
                    item.delete();
                    medicationItemRepository.save(item);
                });

        medicalRecordAttachmentRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId)
                .forEach(item -> {
                    item.delete();
                    medicalRecordAttachmentRepository.save(item);
                    if (item.getFileId() != null) {
                        try {
                            fileService.softDeleteFile(item.getFileId());
                        } catch (Exception e) {
                            log.warn("청구서 파일 삭제 실패: fileId={}, error={}", item.getFileId(), e.getMessage());
                        }
                    }
                });
        if (record.getReceiptFiledId() != null) {
            try {
                fileService.softDeleteFile(record.getReceiptFiledId());
            } catch (Exception e) {
                log.warn("청구서 파일 삭제 실패: fileId={}, error={}", record.getReceiptFiledId(), e.getMessage());
            }
        }

        record.delete();
        medicalRecordRepository.save(record);

        log.info("진료기록 삭제 완료: recordId={}, petId={}, accountId={}", recordId, petId, accountId);
    }

    private MedicalRecord validateAndGetmedicalRecord(Long petId, Long recordId) {
        MedicalRecord record = medicalRecordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new IllegalArgumentException("진료기록을 찾을 수 없습니다."));
        if (!record.getPetId().equals(petId)) {
            throw new IllegalArgumentException("해당 펫의 기록이 아닙니다.");
        }
        return record;
    }

    private FileInfoDto getFileInfo(Long fileId) {
        try {
            MetaDataFile file = fileService.getFileById(fileId);

            // 삭제되었거나 사용 불가능한 파일은 제외
            if (!file.isAvailable()) {
                log.warn("사용 불가능한 파일: fileId={}", fileId);
                return null;
            }

            String fileUrl = fileService.getFileUrl(file.getS3Key());

            return FileInfoDto.builder()
                    .fileId(file.getId())
                    .fileName(file.getOriginalFileName())
                    .fileUrl(fileUrl)
                    .fileSize(file.getFileSize())
                    .contentType(file.getContentType())
                    .build();
        } catch (Exception e) {
            log.error("파일 정보 조회 실패: fileId={}, error={}", fileId, e.getMessage());
            return null;
        }
    }

    private void updateTestItems(Long recordId, List<TestItemDto> newTestItems) {
        List<TestItem> existingItems = testItemRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId);

        if (newTestItems == null || newTestItems.isEmpty()) {
            existingItems.forEach(item -> {
                item.delete();
                testItemRepository.save(item);
            });
            return;
        }

        Map<Long, TestItem> existingMap = existingItems.stream()
                .collect(Collectors.toMap(TestItem::getId, item -> item));

        Set<Long> checkIds = new HashSet<>();

        for(TestItemDto dto : newTestItems) {
            if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
                TestItem existing = existingMap.get(dto.getId());
                existing.update(dto.getName(), dto.getQuantity(), dto.getUnitPrice(),
                        dto.getAmount(), dto.getNotes());
                testItemRepository.save(existing);
                checkIds.add(existing.getId());
            } else {
                TestItem newItem = dto.toTestItem(recordId);
                testItemRepository.save(newItem);
            }
        }

        existingItems.stream()
                .filter(item -> !checkIds.contains(item.getId()))
                .forEach(item -> {
                    item.delete();
                    testItemRepository.save(item);
                });
    }

    private void updateTreatmentItems(Long recordId, List<TreatmentItemDto> newTreatmentItems) {
        List<TreatmentItem> existingItems = treatmentItemRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId);

        if (newTreatmentItems == null || newTreatmentItems.isEmpty()) {
            existingItems.forEach(item -> {
                item.delete();
                treatmentItemRepository.save(item);
            });
            return;
        }

        Map<Long, TreatmentItem> existingMap = existingItems.stream()
                .collect(Collectors.toMap(TreatmentItem::getId, item -> item));

        Set<Long> checkIds = new HashSet<>();

        for(TreatmentItemDto dto : newTreatmentItems) {
            if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
                TreatmentItem existing = existingMap.get(dto.getId());
                existing.update(dto.getName(), dto.getQuantity(), dto.getUnitPrice(),
                        dto.getAmount(), dto.getNotes());
                treatmentItemRepository.save(existing);
                checkIds.add(existing.getId());
            } else {
                TreatmentItem newItem = dto.toTreatmentItem(recordId);
                treatmentItemRepository.save(newItem);
            }
        }

        existingItems.stream()
                .filter(item -> !checkIds.contains(item.getId()))
                .forEach(item -> {
                    item.delete();
                    treatmentItemRepository.save(item);
                });
    }

    private void updateMedicationItems(Long recordId, List<MedicationItemDto> newMedicationItems) {
        List<MedicationItem> existingItems = medicationItemRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId);

        if (newMedicationItems == null || newMedicationItems.isEmpty()) {
            existingItems.forEach(item -> {
                item.delete();
                medicationItemRepository.save(item);
            });
            return;
        }

        Map<Long, MedicationItem> existingMap = existingItems.stream()
                .collect(Collectors.toMap(MedicationItem::getId, item -> item));

        Set<Long> checkIds = new HashSet<>();

        for(MedicationItemDto dto : newMedicationItems) {
            if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
                MedicationItem existing = existingMap.get(dto.getId());
                existing.update(dto.getName(), dto.getQuantity(), dto.getUnitPrice(), dto.getAmount(),
                        dto.getFrequency(), dto.getDays(), dto.getNotes());
                medicationItemRepository.save(existing);
                checkIds.add(existing.getId());
            } else {
                MedicationItem newItem = dto.toMedicationItem(recordId);
                medicationItemRepository.save(newItem);
            }
        }

        existingItems.stream()
                .filter(item -> !checkIds.contains(item.getId()))
                .forEach(item -> {
                    item.delete();
                    medicationItemRepository.save(item);
                });
    }

    private void updateAttachments(Long recordId, List<Long> newFileIds) {
        List<MedicalRecordAttachment> existingAttachments = medicalRecordAttachmentRepository.findByMedicalRecordIdAndIsDeletedFalse(recordId);

        if (newFileIds == null || newFileIds.isEmpty()) {
            existingAttachments.forEach(attachment -> {
                attachment.delete();
                medicalRecordAttachmentRepository.save(attachment);
            });
            return;
        }

        Set<Long> existingFileIds = existingAttachments.stream()
                .map(MedicalRecordAttachment::getFileId)
                .collect(Collectors.toSet());

        Set<Long> newFileIdSet = new HashSet<>(newFileIds);

        newFileIds.stream()
                .filter(fileId -> !existingFileIds.contains(fileId))
                .forEach(fileId -> {
                    MedicalRecordAttachment attachment = new MedicalRecordAttachment(recordId, fileId);
                    medicalRecordAttachmentRepository.save(attachment);
                });

        existingAttachments.stream()
                .filter(attachment -> !newFileIdSet.contains(attachment.getFileId()))
                .forEach(attachment -> {
                    attachment.delete();
                    medicalRecordAttachmentRepository.save(attachment);
                });
    }
}
