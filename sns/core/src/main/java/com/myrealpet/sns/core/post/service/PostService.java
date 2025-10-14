package com.myrealpet.sns.core.post.service;

import com.myrealpet.sns.dto.post.PostRequestDto;
import com.myrealpet.sns.dto.post.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto requestDto, Long accountId, String nickname, String profileImage);
    
    PostResponseDto getPostById(Long postId, Long accountId);
    
    Page<PostResponseDto> getAllPosts(Pageable pageable, Long accountId);
    
    Page<PostResponseDto> getPostsByAccountId(Long accountId, Pageable pageable, Long currentAccountId);
    
    Page<PostResponseDto> getFeedByAccountId(Long accountId, Pageable pageable);
    
    Page<PostResponseDto> searchPosts(String keyword, Pageable pageable, Long accountId);
    
    PostResponseDto updatePost(Long postId, PostRequestDto requestDto, Long accountId);
    
    void deletePost(Long postId, Long accountId);
    
    void likePost(Long postId, Long accountId, String nickname, String profileImage);
    
    void unlikePost(Long postId, Long accountId);
    
    List<PostResponseDto> getTopLikedPosts(Long accountId);
    
    List<PostResponseDto> getRandomPosts(Long accountId);
}
