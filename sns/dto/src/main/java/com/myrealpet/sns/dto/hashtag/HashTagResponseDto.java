package com.myrealpet.sns.dto.hashtag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashTagResponseDto {

    private Long id;
    private String name;
    private int postCount;
    
    public static HashTagResponseDto of(Long id, String name, int postCount) {
        return HashTagResponseDto.builder()
                .id(id)
                .name(name)
                .postCount(postCount)
                .build();
    }
}
