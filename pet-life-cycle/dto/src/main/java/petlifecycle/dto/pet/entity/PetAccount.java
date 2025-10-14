package petlifecycle.dto.pet.entity;

import petlifecycle.dto.metadata.entity.MetaDataFile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PetAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false, length = 50)
    private String name;

    private Long mainBreedId;

    private String customMainBreedName;

    private Long subBreedId;

    @Column(nullable = false)
    private String gender = "UNKNOWN";

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column(nullable = false)
    private Boolean isNeutered = false;
    @Column(nullable = false)
    private Boolean hasMicrochip = false;

    @Digits(integer = 15, fraction = 0, message = "15자리 숫자를 입력해주세요")
    private Long registrationNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_img_id")
    private MetaDataFile profileImg;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_pdf_id")
    private MetaDataFile registrationPdf;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    public PetAccount(Long accountId, String name, Long mainBreedId, String customMainBreedName, Long subBreedId, String gender, LocalDate birthday, Boolean isNeutered, Boolean hasMicrochip, Long registrationNum) {
        this.accountId = accountId;
        this.name = name;
        this.mainBreedId = mainBreedId;
        this.customMainBreedName = customMainBreedName;
        this.subBreedId = subBreedId;
        this.gender = gender;
        this.birthday = birthday;
        this.isNeutered = isNeutered;
        this.hasMicrochip = hasMicrochip;
        this.registrationNum = registrationNum;
    }

    public void setMainBreedId(Long mainBreedId) { this.mainBreedId = mainBreedId; }

    public void setCustomMainBreedName(String customMainBreedName) { this.customMainBreedName = customMainBreedName; }

    public void setSubBreedId(Long subBreedId) { this.subBreedId = subBreedId; }

    public void setProfileImg(MetaDataFile profileImg) {
        this.profileImg = profileImg;
    }

    public void setRegistrationPdf(MetaDataFile registrationPdf) {
        this.registrationPdf = registrationPdf;
    }

    public void update(String name, String gender, LocalDate birthday, Boolean isNeutered, Boolean hasMicrochip, Long registrationNum) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.isNeutered = isNeutered;
        this.hasMicrochip = hasMicrochip;
        this.registrationNum = registrationNum;
    }

    public void delete() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }
}

