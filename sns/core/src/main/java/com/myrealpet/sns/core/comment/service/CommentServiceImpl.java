package com.myrealpet.sns.core.comment.service;

import com.myrealpet.sns.core.comment.entity.Comment;
import com.myrealpet.sns.core.comment.repository.CommentRepository;
import com.myrealpet.sns.core.post.entity.Post;
import com.myrealpet.sns.core.post.repository.PostRepository;
import com.myrealpet.sns.dto.comment.CommentRequestDto;
import com.myrealpet.sns.dto.comment.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto requestDto, Long accountId, String nickname, String profileImage) {
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Comment parent = null;
        if (requestDto.getParentId() != null) {
            parent = commentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .post(post)
                .nickname(nickname)
                .profileImage(profileImage)
                .parent(parent)
                .accountId(accountId)
                .build();

        Comment savedComment = commentRepository.save(comment);

        post.incrementCommentCount();
        postRepository.save(post);

        return CommentResponseDto.of(savedComment.getId(), savedComment.getContent(),
                savedComment.getPost().getId(), savedComment.getNickname(),
                savedComment.getProfileImage(), savedComment.getAccountId(),
                savedComment.getParent() != null ? savedComment.getParent().getId() : null,
                savedComment.getCreatedAt(), savedComment.getUpdatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Page<Comment> comments = commentRepository.findParentCommentsByPost(post, pageable);

        return comments.map(comment -> {
            CommentResponseDto dto = CommentResponseDto.of(comment.getId(), comment.getContent(),
                    comment.getPost().getId(), comment.getNickname(),
                    comment.getProfileImage(), comment.getAccountId(),
                    comment.getParent() != null ? comment.getParent().getId() : null,
                    comment.getCreatedAt(), comment.getUpdatedAt());
            
            List<CommentResponseDto> replies = commentRepository.findRepliesByParentComment(comment)
                    .stream()
                    .map(reply -> CommentResponseDto.of(reply.getId(), reply.getContent(),
                            reply.getPost().getId(), reply.getNickname(),
                            reply.getProfileImage(), reply.getAccountId(),
                            reply.getParent() != null ? reply.getParent().getId() : null,
                            reply.getCreatedAt(), reply.getUpdatedAt()))
                    .collect(Collectors.toList());
            
            dto.setReplies(replies);
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getRepliesByParentId(Long parentId) {
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        return commentRepository.findRepliesByParentComment(parent)
                .stream()
                .map(reply -> CommentResponseDto.of(reply.getId(), reply.getContent(),
                        reply.getPost().getId(), reply.getNickname(),
                        reply.getProfileImage(), reply.getAccountId(),
                        reply.getParent() != null ? reply.getParent().getId() : null,
                        reply.getCreatedAt(), reply.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, String content, Long accountId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAccountId().equals(accountId)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);

        return CommentResponseDto.of(updatedComment.getId(), updatedComment.getContent(),
                updatedComment.getPost().getId(), updatedComment.getNickname(),
                updatedComment.getProfileImage(), updatedComment.getAccountId(),
                updatedComment.getParent() != null ? updatedComment.getParent().getId() : null,
                updatedComment.getCreatedAt(), updatedComment.getUpdatedAt());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long accountId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAccountId().equals(accountId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);

        Post post = comment.getPost();
        post.decrementCommentCount();
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByAccountId(Long accountId, Pageable pageable) {
        return commentRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable)
                .map(comment -> CommentResponseDto.of(comment.getId(), comment.getContent(),
                        comment.getPost().getId(), comment.getNickname(),
                        comment.getProfileImage(), comment.getAccountId(),
                        comment.getParent() != null ? comment.getParent().getId() : null,
                        comment.getCreatedAt(), comment.getUpdatedAt()));
    }
}
