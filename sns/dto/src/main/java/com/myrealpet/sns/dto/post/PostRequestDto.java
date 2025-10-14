package com.myrealpet.sns.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;
    
    private String image;
    
    private List<String> hashtags;
}
