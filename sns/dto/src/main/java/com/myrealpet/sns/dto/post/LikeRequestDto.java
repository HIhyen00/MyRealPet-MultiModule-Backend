package com.myrealpet.sns.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {

    @NotNull(message = "게시물 ID는 필수 입력값입니다.")
    private Long postId;
}
