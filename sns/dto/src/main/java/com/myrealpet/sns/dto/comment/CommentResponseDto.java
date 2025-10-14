package com.myrealpet.sns.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private Long postId;
    private String nickname;
    private String profileImage;
    private Long accountId;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<CommentResponseDto> replies = new ArrayList<>();
    
    public static CommentResponseDto of(Long id, String content, Long postId, String nickname, 
                                       String profileImage, Long accountId, Long parentId,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        return CommentResponseDto.builder()
                .id(id)
                .content(content)
                .postId(postId)
                .nickname(nickname)
                .profileImage(profileImage)
                .accountId(accountId)
                .parentId(parentId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
