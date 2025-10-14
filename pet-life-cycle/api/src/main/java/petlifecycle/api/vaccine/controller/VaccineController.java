package petlifecycle.api.vaccine.controller;

import petlifecycle.client.vaccine.response.VaccineResponse;
import petlifecycle.core.vaccine.service.VaccineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/vaccines")
public class VaccineController {

    private final VaccineService vaccineService;
    //종별 백신 조회
    @GetMapping("/{species}")
    public List<VaccineResponse> getVaccineBySpecies(@PathVariable String species) {
        return vaccineService.getVaccineBySpecies(species)
                .stream()
                .map(VaccineResponse::fromVaccine)
                .collect(Collectors.toList());
    }
}
