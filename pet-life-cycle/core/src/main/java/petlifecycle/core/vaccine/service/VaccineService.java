package petlifecycle.core.vaccine.service;

import petlifecycle.core.vaccine.repository.VaccineRepository;
import petlifecycle.dto.breed.entity.Species;
import petlifecycle.dto.vaccine.entity.Vaccine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineService {
    private final VaccineRepository vaccineRepository;

    public Vaccine createVaccine(Vaccine vaccine) {
        return vaccineRepository.save(vaccine);
    }

    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    //id별 백신 조회
    public Vaccine getVaccineById(long id) {
        return vaccineRepository.findById(id).orElse(null);
    }

    //종에 따라 백신 조회
    public List<Vaccine> getVaccineBySpecies(String species) {
        Species s;
        try{
            s = Species.valueOf(species.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new RuntimeException("유효하지 않은 species 값입니다.");
        }
        List<Vaccine> vaccineList = vaccineRepository.findBySpeciesAndIsDeletedFalseOrderByVaccineIdAsc(s);
        if(vaccineList.isEmpty()){
            throw new RuntimeException("해당 종의 백신이 없습니다..");
        }
        return vaccineList;
    }


    public Vaccine updateVaccine(long id, Vaccine vaccine) {
        Vaccine V = getVaccineById(id);

        V.setVaccineName(vaccine.getVaccineName());
        V.setDescription(vaccine.getDescription());
        V.setSideEffects(vaccine.getSideEffects());
        V.setVaccineCycle(vaccine.getVaccineCycle());
        V.setSpecies(vaccine.getSpecies());

        return vaccineRepository.save(V);
    }

    public void deleteVaccine(long id) {

        Vaccine V = getVaccineById(id);
        V.delete();
        vaccineRepository.save(V);
    }
}