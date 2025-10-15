package qna.api.answer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qna.client.ReportRequest;
import qna.client.VoteRequest;
import qna.client.answer.request.AnswerCreateRequest;
import qna.client.answer.request.AnswerUpdateRequest;
import qna.client.answer.response.AnswerResponse;
import qna.core.answer.service.AnswerService;

@RestController
@RequestMapping("/api/qna/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestAttribute("userId") Long userId,
                                       @Valid @RequestBody AnswerCreateRequest req) {
        Long id = answerService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<Page<AnswerResponse>> listByQuestion(
            @RequestParam Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(answerService.list(questionId, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody AnswerUpdateRequest req) {
        answerService.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long id) {
        answerService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/votes")
    public ResponseEntity<Void> vote(@RequestAttribute("userId") Long userId,
                                     @PathVariable("id") Long answerId,
                                     @Valid @RequestBody VoteRequest req) {
        answerService.vote(userId, answerId, req.type());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/reports")
    public ResponseEntity<Void> report(@RequestAttribute("userId") Long userId,
                                       @PathVariable("id") Long answerId,
                                       @Valid @RequestBody ReportRequest req) {
        answerService.report(userId, answerId, req.reason());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}