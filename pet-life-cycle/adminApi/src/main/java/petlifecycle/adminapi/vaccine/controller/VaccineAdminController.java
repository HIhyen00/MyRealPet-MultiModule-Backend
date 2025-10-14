package petlifecycle.adminapi.vaccine.controller;

import petlifecycle.client.vaccine.request.VaccineRequest;
import petlifecycle.client.vaccine.response.VaccineResponse;
import petlifecycle.core.vaccine.service.VaccineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/pets/vaccines")
public class VaccineAdminController {
    private final VaccineService vaccineService;

    @PostMapping
    public VaccineResponse createVaccine(@RequestBody VaccineRequest request, @RequestAttribute("userId") Long adminId) {
        return VaccineResponse.fromVaccine(vaccineService.createVaccine(request.toEntity()));
    }

    @GetMapping
    public List<VaccineResponse> getAllVaccines(@RequestAttribute("userId") Long userId) {
        return vaccineService.getAllVaccines()
                .stream()
                .map(VaccineResponse::fromVaccine)
                .collect(Collectors.toList());
    }

    @GetMapping("/{vaccineId}")
    public VaccineResponse getVaccineById(@PathVariable Long vaccineId, @RequestAttribute("userId") Long adminId) {
        return VaccineResponse.fromVaccine(vaccineService.getVaccineById(vaccineId));
    }

    @PutMapping("/{vaccineId}")
    public VaccineResponse updateVaccine(@PathVariable Long vaccineId, @RequestBody VaccineRequest request
            , @RequestAttribute("userId") Long adminId) {
        return VaccineResponse.fromVaccine(vaccineService.updateVaccine(vaccineId, request.toEntity()));
    }

    @DeleteMapping("/{vaccineId}")
    public void deleteVaccine(@PathVariable Long vaccineId, @RequestAttribute("userId") Long adminId) {
        vaccineService.deleteVaccine(vaccineId);
    }

}
