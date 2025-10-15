package qna.core.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qna.dto.answers.entity.AnswerReport;

@Repository
public interface AnswerReportRepository extends JpaRepository<AnswerReport, Long> {
    boolean existsByAnswerIdAndReporterId(Long answerId, Long reporterId);
}
