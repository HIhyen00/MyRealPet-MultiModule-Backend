package petlifecycle.client.pet.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class UpdatePetAccountRequest {
    private final String name;
    private final Long mainBreedId;
    private final String customMainBreedName;
    private final Long subBreedId;
    private final String gender;
    private final LocalDate birthday;
    private final Boolean isNeutered;
    private final Boolean hasMicrochip;
    private final Long registrationNum;
    private final MultipartFile profileImg;
    private final MultipartFile registrationPdf;

    private final Boolean deleteProfileImg;
    private final Boolean deleteRegistrationPdf;

    public boolean hasProfileImg() {
        return profileImg != null && !profileImg.isEmpty();
    }

    public boolean hasRegisterPdf() {
        return registrationPdf != null && !registrationPdf.isEmpty();
    }

}
