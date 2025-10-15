package qna.dto.answers.entity;

public enum AnswerStatus {
    VISIBLE,   // 정상 노출
    BLINDED,   // 신고 누적 → 블라인드 처리
    DELETED    // 삭제됨 (soft delete)
}
