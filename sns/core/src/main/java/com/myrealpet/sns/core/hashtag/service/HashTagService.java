package com.myrealpet.sns.core.hashtag.service;

import com.myrealpet.sns.dto.hashtag.HashTagResponseDto;
import com.myrealpet.sns.dto.post.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HashTagService {

    HashTagResponseDto registerHashTag(String tagName);
    
    List<HashTagResponseDto> getPopularHashTags(int limit);
    
    Page<PostResponseDto> getPostsByHashTag(String tagName, Pageable pageable, Long accountId);
    
    List<HashTagResponseDto> searchHashTags(String keyword);
}
