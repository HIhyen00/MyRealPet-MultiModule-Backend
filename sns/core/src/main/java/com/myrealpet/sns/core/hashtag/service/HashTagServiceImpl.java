package com.myrealpet.sns.core.hashtag.service;

import com.myrealpet.sns.core.hashtag.entity.HashTag;
import com.myrealpet.sns.core.hashtag.entity.HashTagToPost;
import com.myrealpet.sns.core.hashtag.repository.HashTagRepository;
import com.myrealpet.sns.core.hashtag.repository.HashTagToPostRepository;
import com.myrealpet.sns.core.post.entity.Post;
import com.myrealpet.sns.core.post.repository.LikeRepository;
import com.myrealpet.sns.core.post.repository.PostRepository;
import com.myrealpet.sns.dto.hashtag.HashTagResponseDto;
import com.myrealpet.sns.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {

    private final HashTagRepository hashTagRepository;
    private final HashTagToPostRepository hashTagToPostRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public HashTagResponseDto registerHashTag(String tagName) {
        HashTag hashTag = hashTagRepository.findByName(tagName)
                .orElseGet(() -> hashTagRepository.save(HashTag.builder().name(tagName).build()));
        
        int postCount = hashTagToPostRepository.findByHashTag(hashTag).size();
        
        return HashTagResponseDto.of(hashTag.getId(), hashTag.getName(), postCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HashTagResponseDto> getPopularHashTags(int limit) {
        List<HashTag> allHashTags = hashTagRepository.findAll();
        
        List<HashTagResponseDto> hashTagDtos = new ArrayList<>();
        for (HashTag hashTag : allHashTags) {
            int postCount = hashTagToPostRepository.findByHashTag(hashTag).size();
            hashTagDtos.add(HashTagResponseDto.of(hashTag.getId(), hashTag.getName(), postCount));
        }
        
        return hashTagDtos.stream()
                .sorted(Comparator.comparing(HashTagResponseDto::getPostCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByHashTag(String tagName, Pageable pageable, Long accountId) {
        HashTag hashTag = hashTagRepository.findByName(tagName)
                .orElseThrow(() -> new IllegalArgumentException("해시태그를 찾을 수 없습니다."));
        
        List<Long> postIds = hashTagToPostRepository.findByHashTag(hashTag).stream()
                .map(hashTagToPost -> hashTagToPost.getPost().getId())
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postIds.size());
        
        List<Long> pagedPostIds = postIds.subList(start, end);
        
        List<PostResponseDto> postDtos = new ArrayList<>();
        for (Long postId : pagedPostIds) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
            
            boolean isLiked = likeRepository.existsByPostIdAndAccountId(postId, accountId);
            
            List<String> hashtags = hashTagToPostRepository.findByPostId(postId).stream()
                    .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                    .collect(Collectors.toList());
            
            postDtos.add(PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                    post.getImage(), post.getNickname(), post.getProfileImage(),
                    post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                    isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt()));
        }
        
        return new PageImpl<>(postDtos, pageable, postIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HashTagResponseDto> searchHashTags(String keyword) {
        List<HashTag> hashTags = hashTagRepository.findByNameContaining(keyword);
        
        return hashTags.stream()
                .map(hashTag -> {
                    int postCount = hashTagToPostRepository.findByHashTag(hashTag).size();
                    return HashTagResponseDto.of(hashTag.getId(), hashTag.getName(), postCount);
                })
                .collect(Collectors.toList());
    }
}
