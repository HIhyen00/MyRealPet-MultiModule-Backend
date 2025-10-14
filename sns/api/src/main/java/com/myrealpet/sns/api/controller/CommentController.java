package com.myrealpet.sns.api.controller;

import com.myrealpet.sns.core.comment.service.CommentService;
import com.myrealpet.sns.core.redis.RedisCacheService;
import com.myrealpet.sns.dto.comment.CommentRequestDto;
import com.myrealpet.sns.dto.comment.CommentResponseDto;
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
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final RedisCacheService redisCacheService;
    
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CommentRequestDto requestDto) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        String nickname = "사용자" + accountId;
        String profileImage = null;
        
        CommentResponseDto responseDto = commentService.createComment(requestDto, accountId, nickname, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByPostId(
            @PathVariable Long postId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<CommentResponseDto> comments = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponseDto>> getRepliesByParentId(
            @PathVariable Long commentId) {
        
        List<CommentResponseDto> replies = commentService.getRepliesByParentId(commentId);
        return ResponseEntity.ok(replies);
    }
    
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long commentId,
            @RequestParam String content) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        CommentResponseDto responseDto = commentService.updateComment(commentId, content, accountId);
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long commentId) {
        
        String token = authHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        commentService.deleteComment(commentId, accountId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{accountId}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByAccountId(
            @PathVariable Long accountId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<CommentResponseDto> comments = commentService.getCommentsByAccountId(accountId, pageable);
        return ResponseEntity.ok(comments);
    }
}
