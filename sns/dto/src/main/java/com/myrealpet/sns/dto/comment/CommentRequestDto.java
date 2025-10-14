package com.myrealpet.sns.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @NotNull(message = "게시물 ID는 필수 입력값입니다.")
    private Long postId;
    
    @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
    private String content;
    
    private Long parentId;
}
