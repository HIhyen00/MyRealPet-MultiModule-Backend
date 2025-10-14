package com.myrealpet.sns.core.hashtag.repository;

import com.myrealpet.sns.core.hashtag.entity.HashTag;
import com.myrealpet.sns.core.hashtag.entity.HashTagToPost;
import com.myrealpet.sns.core.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashTagToPostRepository extends JpaRepository<HashTagToPost, Long> {
    
    List<HashTagToPost> findByPostId(Long postId);
    
    List<HashTagToPost> findByHashTag(HashTag hashTag);
    
    List<HashTagToPost> findByPost(Post post);
    
    void deleteByPostId(Long postId);
}
