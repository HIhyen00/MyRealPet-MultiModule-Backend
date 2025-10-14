package com.myrealpet.sns.core.comment.service;

import com.myrealpet.sns.dto.comment.CommentRequestDto;
import com.myrealpet.sns.dto.comment.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(CommentRequestDto requestDto, Long accountId, String nickname, String profileImage);
    
    Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable);
    
    List<CommentResponseDto> getRepliesByParentId(Long parentId);
    
    CommentResponseDto updateComment(Long commentId, String content, Long accountId);
    
    void deleteComment(Long commentId, Long accountId);
    
    Page<CommentResponseDto> getCommentsByAccountId(Long accountId, Pageable pageable);
}
