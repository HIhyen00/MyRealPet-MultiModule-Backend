package com.myrealpet.sns.core.post.repository;

import com.myrealpet.sns.core.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostIdAndAccountId(Long postId, Long accountId);
    
    Optional<Like> findByPostIdAndAccountId(Long postId, Long accountId);
    
    List<Like> findByPostId(Long postId);
    
    List<Like> findByAccountId(Long accountId);
    
    void deleteByPostIdAndAccountId(Long postId, Long accountId);
    
    int countByPostId(Long postId);
}
