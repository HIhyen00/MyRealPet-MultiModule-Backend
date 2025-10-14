package com.myrealpet.sns.core.comment.repository;

import com.myrealpet.sns.core.comment.entity.Comment;
import com.myrealpet.sns.core.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);
    
    Page<Comment> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
    
    Page<Comment> findByNicknameOrderByCreatedAtDesc(String nickname, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.parent IS NULL AND c.post = :post AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findParentCommentsByPost(@Param("post") Post post, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentComment(@Param("parent") Comment parent);
    
    int countByPost(Post post);
    
    int countByPostAndDeletedAtIsNull(Post post);
}
