package com.myrealpet.sns.core.post.repository;

import com.myrealpet.sns.core.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
    
    Page<Post> findByNicknameOrderByCreatedAtDesc(String nickname, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    Page<Post> findAllActivePosts(Pageable pageable);
    
    @Query(value = "SELECT p.* FROM posts p " +
            "WHERE p.account_id IN (:followingAccountIds) AND p.deleted_at IS NULL " +
            "ORDER BY p.created_at DESC", nativeQuery = true)
    Page<Post> findFeedByFollowingAccountIds(@Param("followingAccountIds") List<Long> followingAccountIds, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% AND p.deletedAt IS NULL")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    List<Post> findTop10ByOrderByLikeCountDesc();
    
    List<Post> findTop10ByDeletedAtIsNullOrderByLikeCountDesc();
    
    @Query(value = "SELECT * FROM posts p WHERE p.deleted_at IS NULL ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Post> findRandomPosts();
}
