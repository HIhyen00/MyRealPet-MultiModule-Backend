package petlifecycle.dto.healthnote.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "health_notes")
public class HealthNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ToDo: Pet 엔티티와 연관관계 매핑 필요
    @Column(nullable = false)
    private Long petId;

    @Column(nullable = false)
    private LocalDate recordDate;

    private String mood; // "good", "normal", "bad"

    private String poop; // "good", "normal", "bad"

    private String pee;  // "good", "normal", "bad"

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    public static HealthNote create(Long petId, LocalDate recordDate, String mood, String poop, String pee, String symptoms) {
        HealthNote healthNote = new HealthNote();
        healthNote.petId = petId;
        healthNote.recordDate = recordDate;
        healthNote.mood = mood;
        healthNote.poop = poop;
        healthNote.pee = pee;
        healthNote.symptoms = symptoms;
        return healthNote;
    }
}
