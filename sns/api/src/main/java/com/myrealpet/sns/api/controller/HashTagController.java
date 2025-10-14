package com.myrealpet.sns.api.controller;

import com.myrealpet.sns.core.hashtag.service.HashTagService;
import com.myrealpet.sns.core.redis.RedisCacheService;
import com.myrealpet.sns.dto.hashtag.HashTagResponseDto;
import com.myrealpet.sns.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hashtags")
public class HashTagController {

    private final HashTagService hashTagService;
    private final RedisCacheService redisCacheService;
    
    @GetMapping("/popular")
    public ResponseEntity<List<HashTagResponseDto>> getPopularHashTags() {
        List<HashTagResponseDto> hashtags = hashTagService.getPopularHashTags(10);
        return ResponseEntity.ok(hashtags);
    }
    
    @GetMapping("/{tagName}/posts")
    public ResponseEntity<Page<PostResponseDto>> getPostsByHashTag(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String tagName,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        Page<PostResponseDto> posts = hashTagService.getPostsByHashTag(tagName, pageable, accountId);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<HashTagResponseDto>> searchHashTags(
            @RequestParam String keyword) {
        
        List<HashTagResponseDto> hashtags = hashTagService.searchHashTags(keyword);
        return ResponseEntity.ok(hashtags);
    }
}
