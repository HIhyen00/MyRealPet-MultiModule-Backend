package com.myrealpet.sns.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String image;
    private String nickname;
    private String profileImage;
    private Long accountId;
    private int likeCount;
    private int commentCount;
    
    @JsonProperty("isLiked")
    private boolean isLiked;
    private List<String> hashtags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PostResponseDto of(Long id, String title, String content, String image,
                                    String nickname, String profileImage, Long accountId,
                                    int likeCount, int commentCount, boolean isLiked,
                                    List<String> hashtags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return PostResponseDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .image(image)
                .nickname(nickname)
                .profileImage(profileImage)
                .accountId(accountId)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .isLiked(isLiked)
                .hashtags(hashtags)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
