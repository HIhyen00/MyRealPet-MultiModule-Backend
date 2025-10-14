package com.myrealpet.sns.api.controller;

import com.myrealpet.sns.client.AccountApiClient;
import com.myrealpet.sns.core.post.service.PostService;
import com.myrealpet.sns.core.redis.RedisCacheService;
import com.myrealpet.sns.dto.post.PostRequestDto;
import com.myrealpet.sns.dto.post.PostResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final RedisCacheService redisCacheService;
    private final AccountApiClient accountApiClient;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PostRequestDto requestDto) {

        log.info("게시물 생성 요청 옴");
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        // Redis 세션에서 username 가져오기
        String username = redisCacheService.getUsernameByToken(token);
        if (username == null) {
            username = "익명"; // 기본값
        }

        PostResponseDto responseDto = postService.createPost(requestDto, accountId, username, requestDto.getImage());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);

        PostResponseDto responseDto = postService.getPostById(postId, accountId);
        return ResponseEntity.ok(responseDto);
    }
    
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestHeader("Authorization") String authHeader,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        Page<PostResponseDto> posts = postService.getAllPosts(pageable, accountId);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/user/{accountId}")
    public ResponseEntity<Page<PostResponseDto>> getPostsByAccountId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long accountId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long currentAccountId = redisCacheService.getValueByKey(token, Long.class);
        
        Page<PostResponseDto> posts = postService.getPostsByAccountId(accountId, pageable, currentAccountId);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponseDto>> getFeed(
            @RequestHeader("Authorization") String authHeader,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        Page<PostResponseDto> posts = postService.getFeedByAccountId(accountId, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDto>> searchPosts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        Page<PostResponseDto> posts = postService.searchPosts(keyword, pageable, accountId);
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequestDto requestDto) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        PostResponseDto responseDto = postService.updatePost(postId, requestDto, accountId);
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        postService.deletePost(postId, accountId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "게시물이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, String>> likePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        String nickname = "사용자" + accountId;
        String profileImage = null;
        
        postService.likePost(postId, accountId, nickname, profileImage);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "게시물에 좋아요를 표시했습니다.");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Map<String, String>> unlikePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        postService.unlikePost(postId, accountId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "게시물 좋아요를 취소했습니다.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/top")
    public ResponseEntity<List<PostResponseDto>> getTopLikedPosts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long accountId = null;
        if (authHeader != null && !authHeader.isEmpty()) {
            String token = authHeader.replace("Bearer ", "").trim();
            accountId = redisCacheService.getValueByKey(token, Long.class);
        }
        
        List<PostResponseDto> posts = postService.getTopLikedPosts(accountId);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/random")
    public ResponseEntity<List<PostResponseDto>> getRandomPosts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("랜덤 게시물 조회 요청 - Authorization 헤더: {}", authHeader != null ? "있음" : "없음");
        
        Long accountId = null;
        if (authHeader != null && !authHeader.isEmpty()) {
            String token = authHeader.replace("Bearer ", "").trim();
            accountId = redisCacheService.getValueByKey(token, Long.class);
            log.info("토큰에서 추출한 accountId: {}", accountId);
        } else {
            log.warn("Authorization 헤더가 없습니다. 비로그인 상태로 처리합니다.");
        }
        
        List<PostResponseDto> posts = postService.getRandomPosts(accountId);
        log.info("랜덤 게시물 {} 개 반환 (accountId: {})", posts.size(), accountId);
        return ResponseEntity.ok(posts);
    }
}
