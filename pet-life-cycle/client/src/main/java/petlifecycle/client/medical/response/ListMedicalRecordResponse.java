package petlifecycle.client.medical.response;

import petlifecycle.dto.medical.entity.MedicalRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class ListMedicalRecordResponse {
    private final List<Map<String, Object>> medicalRecordList;
    private final int currentPage;
    private final int totalPages;
    private final long totalItems;

    public List<Map<String, Object>> toMedicalRecordList() {
        return medicalRecordList;
    }

    public static ListMedicalRecordResponse from(List<MedicalRecord> toMedicalRecordList, int currentPage, int totalPages, long totalItems) {
        List<Map<String, Object>> medicalRecordList = toMedicalRecordList.stream()
                .map(medicalRecord -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", medicalRecord.getId());
                    map.put("hospitalName", medicalRecord.getHospitalName());
                    map.put("visitDate", medicalRecord.getVisitDate());
                    map.put("diagnosis", medicalRecord.getDiagnosis());
                    return map;
                })
                .collect(Collectors.toList());
        return new ListMedicalRecordResponse(medicalRecordList, currentPage, totalPages, totalItems);
    }
}
