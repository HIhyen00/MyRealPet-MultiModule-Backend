package qna.api.question.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qna.client.question.request.QuestionCreateRequest;
import qna.client.question.request.QuestionUpdateRequest;
import qna.client.question.response.QuestionResponse;
import qna.core.question.service.QuestionService;

@RestController
@RequestMapping("/api/qna/questions")
@RequiredArgsConstructor

public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestAttribute("userId") Long userId,
                                       @Valid @RequestBody QuestionCreateRequest req) {
        Long id = questionService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        if("ALL".equalsIgnoreCase(category)) category = null;
        return ResponseEntity.ok(questionService.list(category, sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody QuestionUpdateRequest req) {
        questionService.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long id) {
        questionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stats")
    public ResponseEntity<Void> updateStats(@PathVariable Long id,
                                            @RequestParam String type) {
        questionService.updateStats(id, type);
        return ResponseEntity.ok().build();
    }
}
