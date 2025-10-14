package petlifecycle.client.pet.response;

import petlifecycle.dto.breed.entity.Breed;
import petlifecycle.dto.breed.entity.Species;
import petlifecycle.dto.pet.entity.PetAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReadPetAccountResponse {
    private final Long petId;
    private final String name;
    private final Species species;
    private final Long mainBreedId;
    private final String mainBreedName;
    private final String customMainBreedName;
    private final Long subBreedId;
    private final String subBreedName;
    private final String gender;
    private final LocalDate birthday;
    private final Boolean isNeutered;
    private final Boolean hasMicrochip;
    private final Long registrationNum;
    private String profileImgUrl;
    private String registrationPdfUrl;
    private final LocalDateTime createdAt;

    public static ReadPetAccountResponse from(PetAccount petAccount, Breed mainBreed, Breed subBreed, String profileImgUrl, String registrationPdfUrl) {

        return ReadPetAccountResponse.builder()
                .petId(petAccount.getPetId())
                .name(petAccount.getName())
                .species(mainBreed != null ? mainBreed.getSpecies() : Species.OTHER)
                .mainBreedId(mainBreed != null ? mainBreed.getId() : null)
                .mainBreedName(mainBreed != null ? mainBreed.getName() : null)
                .customMainBreedName(petAccount.getCustomMainBreedName())
                .subBreedId(subBreed != null ? subBreed.getId() : null)
                .subBreedName(subBreed != null ? subBreed.getName() : null)
                .gender(petAccount.getGender())
                .birthday(petAccount.getBirthday())
                .isNeutered(petAccount.getIsNeutered())
                .hasMicrochip(petAccount.getHasMicrochip())
                .registrationNum(petAccount.getRegistrationNum())
                .profileImgUrl(profileImgUrl)
                .registrationPdfUrl(registrationPdfUrl)
                .createdAt(petAccount.getCreatedAt()) // LocalDateTime.now() -> petAccount.getCreatedAt()
                .build();
    }
}
