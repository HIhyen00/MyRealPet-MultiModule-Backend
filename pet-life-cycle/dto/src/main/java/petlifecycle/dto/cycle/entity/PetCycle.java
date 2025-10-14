package petlifecycle.dto.cycle.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet_cycles")
public class PetCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ToDo: Pet 엔티티와 연관관계 매핑 필요
    @Column(nullable = false)
    private Long petId;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private String memo;

    public static PetCycle create(Long petId, LocalDate startDate, LocalDate endDate, String memo) {
        PetCycle petCycle = new PetCycle();
        petCycle.petId = petId;
        petCycle.startDate = startDate;
        petCycle.endDate = endDate;
        petCycle.memo = memo;
        return petCycle;
    }
}
