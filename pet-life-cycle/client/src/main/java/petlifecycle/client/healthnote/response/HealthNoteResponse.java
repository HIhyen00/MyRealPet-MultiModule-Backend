package petlifecycle.client.healthnote.response;

import lombok.Getter;
import petlifecycle.dto.healthnote.entity.HealthNote;

import java.time.LocalDate;

@Getter
public class HealthNoteResponse {
    private Long id;
    private LocalDate recordDate;
    private String mood;
    private String poop;
    private String pee;
    private String symptoms;

    private HealthNoteResponse(Long id, LocalDate recordDate, String mood, String poop, String pee, String symptoms) {
        this.id = id;
        this.recordDate = recordDate;
        this.mood = mood;
        this.poop = poop;
        this.pee = pee;
        this.symptoms = symptoms;
    }

    public static HealthNoteResponse from(HealthNote healthNote) {
        return new HealthNoteResponse(
                healthNote.getId(),
                healthNote.getRecordDate(),
                healthNote.getMood(),
                healthNote.getPoop(),
                healthNote.getPee(),
                healthNote.getSymptoms()
        );
    }
}