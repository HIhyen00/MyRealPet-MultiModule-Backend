package qna.core.answer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qna.dto.answers.entity.Answer;
import qna.dto.answers.entity.AnswerStatus;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Page<Answer> findByQuestionId(Long questionId, Pageable pageable);
    Page<Answer> findByQuestionIdAndStatus(Long questionId, AnswerStatus status, Pageable pageable);


}
