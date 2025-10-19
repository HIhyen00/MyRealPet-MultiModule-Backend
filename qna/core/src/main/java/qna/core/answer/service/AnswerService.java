package qna.core.answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qna.client.answer.request.AnswerCreateRequest;
import qna.client.answer.request.AnswerUpdateRequest;
import qna.client.answer.response.AnswerResponse;
import qna.core.answer.repository.AnswerReportRepository;
import qna.core.answer.repository.AnswerRepository;
import qna.core.answer.repository.AnswerVoteRepository;
import qna.core.question.repository.QuestionRepository;
import qna.core.question.service.QuestionService;
import qna.dto.answers.entity.*;
import qna.dto.questions.entity.Question;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {
    private static final int REPORT_BLIND_THRESHOLD = 5;

    private final AnswerRepository answerRepository;
    private final AnswerVoteRepository answerVoteRepository;
    private final AnswerReportRepository answerReportRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    public Long create(Long userId, AnswerCreateRequest req) {
        Question question = questionRepository.findById(req.questionId())
                .orElseThrow(() -> new IllegalArgumentException("question not found"));

        Answer a = Answer.builder()
                .questionId(req.questionId())
                .userId(userId)
                .content(req.content())
                .isPrivate(req.isPrivate())
                .status(AnswerStatus.VISIBLE)
                .upvoteCount(0)
                .downvoteCount(0)
                .reportCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Answer savedAnswer = answerRepository.save(a);

        //answer increase
        questionService.updateStats(req.questionId(), "answer");

        return savedAnswer.getId();
    }

    @Transactional(readOnly = true)
    public Page<AnswerResponse> list(Long questionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Answer> result = answerRepository.findByQuestionId(questionId, pageable);
        // DELETED 상태인 답변은 제외하고 반환
        return result
                .filter(a -> a.getStatus() != AnswerStatus.DELETED)
                .map(AnswerResponse::of);
    }

    public void update(Long userId, Long answerId, AnswerUpdateRequest req) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));
        validateAuthor(userId, a);
        a.setContent(req.content());
        a.setPrivate(req.isPrivate()); // primitive boolean
        a.setUpdatedAt(LocalDateTime.now());
    }

    public void delete(Long userId, Long answerId) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));
        validateAuthor(userId, a);
        a.setStatus(AnswerStatus.DELETED);
        a.setUpdatedAt(LocalDateTime.now());

        //delete
        questionService.updateStats(a.getQuestionId(), "answer");
    }




    public void vote(Long userId, Long answerId, String typeRaw) {
        VoteType type = toVoteType(typeRaw);

        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));

        AnswerVote existing = answerVoteRepository.findByAnswerIdAndUserId(answerId, userId).orElse(null);

        if (existing == null) {
            AnswerVote v = AnswerVote.builder()
                    .answerId(answerId)
                    .userId(userId)
                    .type(type)
                    .createdAt(LocalDateTime.now())
                    .build();
            answerVoteRepository.save(v);
            applyVoteDelta(a, type, +1);
        } else {
            if (existing.getType() == type) {
                answerVoteRepository.delete(existing); // 같은 타입 → 해제
                applyVoteDelta(a, type, -1);
            } else {
                applyVoteDelta(a, existing.getType(), -1);
                existing.setType(type);
                applyVoteDelta(a, type, +1);
            }
        }

        normalizeCounts(a);
        a.setUpdatedAt(LocalDateTime.now());

        //update
        questionService.updateStats(a.getQuestionId(), "like");
    }

    public boolean report(Long userId, Long answerId, String reason) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));

        if (answerReportRepository.existsByAnswerIdAndReporterId(answerId, userId)) {
            return false; //중복신고 시 false 리턴
        }

        AnswerReport report = AnswerReport.builder()
                .answerId(answerId)
                .reporterId(userId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        answerReportRepository.save(report);

        a.setReportCount(a.getReportCount() + 1);
        if (a.getReportCount() >= REPORT_BLIND_THRESHOLD && a.getStatus() == AnswerStatus.VISIBLE) {
            a.setStatus(AnswerStatus.BLINDED);
        }
        a.setUpdatedAt(LocalDateTime.now());
        return true;
    }

    // helpers
    private void validateAuthor(Long userId, Answer a) {
        if (userId == null || !a.getUserId().equals(userId)) {
            throw new IllegalStateException("only author can modify/delete");
        }
    }

    private VoteType toVoteType(String raw) {
        if (raw == null) throw new IllegalArgumentException("vote type is required");
        try {
            return VoteType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid vote type: " + raw);
        }
    }

    private void applyVoteDelta(Answer a, VoteType type, int delta) {
        switch (type) {
            case UP -> a.setUpvoteCount(a.getUpvoteCount() + delta);
            case DOWN -> a.setDownvoteCount(a.getDownvoteCount() + delta);
        }
    }

    private void normalizeCounts(Answer a) {
        if (a.getUpvoteCount() < 0) a.setUpvoteCount(0);
        if (a.getDownvoteCount() < 0) a.setDownvoteCount(0);
        if (a.getReportCount() < 0) a.setReportCount(0);
    }
}
