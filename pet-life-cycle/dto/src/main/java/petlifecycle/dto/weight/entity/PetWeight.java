package petlifecycle.dto.weight.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet_weights")
public class PetWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ToDo: Pet 엔티티와 연관관계 매핑 필요
    @Column(nullable = false)
    private Long petId;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private LocalDate recordDate;

    public static PetWeight create(Long petId, Double weight, LocalDate recordDate) {
        PetWeight petWeight = new PetWeight();
        petWeight.petId = petId;
        petWeight.weight = weight;
        petWeight.recordDate = recordDate;
        return petWeight;
    }
}
