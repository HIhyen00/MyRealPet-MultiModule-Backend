package qna.core.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qna.dto.questions.entity.QuestionStat;

@Repository
public interface QuestionStatRepository extends JpaRepository<QuestionStat, Long> {

}
