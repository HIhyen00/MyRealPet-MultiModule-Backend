package petlifecycle.api.healthnote.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petlifecycle.client.healthnote.request.HealthNoteCreateRequest;
import petlifecycle.client.healthnote.response.HealthNoteResponse;
import petlifecycle.core.healthnote.service.HealthNoteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets/{petId}/health-notes")
public class HealthNoteController {

    private final HealthNoteService healthNoteService;

    @PostMapping
    public ResponseEntity<Void> createHealthNote(@PathVariable Long petId, @RequestBody HealthNoteCreateRequest request,
                                                 @RequestAttribute("userId") Long userId) {
        healthNoteService.saveHealthNote(petId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<HealthNoteResponse>> getHealthNotes(@PathVariable Long petId, @RequestAttribute("userId") Long userId) {
        List<HealthNoteResponse> notes = healthNoteService.findHealthNotes(petId, userId);
        return ResponseEntity.ok(notes);
    }
}
