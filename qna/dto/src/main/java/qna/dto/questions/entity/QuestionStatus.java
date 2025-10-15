package qna.dto.questions.entity;

public enum QuestionStatus {
    OPEN,     // 답변 가능
    CLOSED,   // 답변 마감
    BLINDED,  // 블라인드 처리
    DELETED   // 삭제(soft delete)
}
