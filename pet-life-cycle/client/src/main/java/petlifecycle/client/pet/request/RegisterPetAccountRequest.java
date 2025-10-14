package petlifecycle.client.pet.request;

import petlifecycle.dto.pet.entity.PetAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterPetAccountRequest {
    private String name;
    private Long mainBreedId;
    private String customMainBreedName;
    private Long subBreedId;
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Boolean isNeutered;
    private Boolean hasMicrochip;
    private Long registrationNum;
    private MultipartFile profileImg;
    private MultipartFile registrationPdf;

    public PetAccount toPetAccount(Long accountId) {

        return new PetAccount(
                accountId,
                name,
                mainBreedId,
                customMainBreedName,
                subBreedId,
                gender,
                birthday,
                isNeutered,
                hasMicrochip,
                registrationNum
        );
    }

    // 파일이 있는지 체크
    public boolean hasProfileImg() {
        return profileImg != null && !profileImg.isEmpty();
    }

    public boolean hasRegisterPdf() {
        return registrationPdf != null && !registrationPdf.isEmpty();
    }
}
