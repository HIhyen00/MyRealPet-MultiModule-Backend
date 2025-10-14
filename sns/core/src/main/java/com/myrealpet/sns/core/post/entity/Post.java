package com.myrealpet.sns.core.post.entity;

import com.myrealpet.sns.core.comment.entity.Comment;
import com.myrealpet.sns.core.hashtag.entity.HashTagToPost;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    private String image;

    @Column(nullable = false)
    private String nickname;
    
    @Column
    private String profileImage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_to_post_id")
    private HashTagToPost hashTag;

    @Column(nullable = false)
    private Long accountId;

    @Builder.Default
    private int likeCount = 0;
    
    @Builder.Default
    private int commentCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    public void incrementCommentCount() {
        this.commentCount++;
    }
    
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}
