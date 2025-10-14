package petlifecycle.dto.breed.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Species species;

    private String name;
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    public Breed(Species species, String name, String description) {
        this.species = species;
        this.name = name;
        this.description = description;
    }

    public void update(String name, String description, Species species) {
        this.name = name;
        this.description = description;
        this.species = species;
    }

    public void delete() {
        isDeleted = true;
    }
}
