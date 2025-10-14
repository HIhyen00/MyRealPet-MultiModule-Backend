package com.myrealpet.sns.core.hashtag.entity;

import com.myrealpet.sns.core.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hashtag_to_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HashTagToPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_to_post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hashtag_id", nullable = false)
    private HashTag hashTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
