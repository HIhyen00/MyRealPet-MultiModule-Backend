package com.myrealpet.sns.core.post.service;

import com.myrealpet.sns.core.hashtag.entity.HashTag;
import com.myrealpet.sns.core.hashtag.entity.HashTagToPost;
import com.myrealpet.sns.core.hashtag.repository.HashTagRepository;
import com.myrealpet.sns.core.hashtag.repository.HashTagToPostRepository;
import com.myrealpet.sns.core.post.entity.Like;
import com.myrealpet.sns.core.post.entity.Post;
import com.myrealpet.sns.core.post.repository.LikeRepository;
import com.myrealpet.sns.core.post.repository.PostRepository;
import com.myrealpet.sns.dto.post.PostRequestDto;
import com.myrealpet.sns.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final HashTagRepository hashTagRepository;
    private final HashTagToPostRepository hashTagToPostRepository;

    @Override
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, Long accountId, String nickname, String profileImage) {
        log.info("게시물 생성 - accountId: {}, nickname: {}, image: {}", accountId, nickname, requestDto.getImage());

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .image(requestDto.getImage())
                .nickname(nickname)
                .profileImage(profileImage)
                .accountId(accountId)
                .build();

        Post savedPost = postRepository.save(post);
        log.info("게시물 저장 완료 - postId: {}, image: {}", savedPost.getId(), savedPost.getImage());

        List<String> hashtags = new ArrayList<>();
        if (requestDto.getHashtags() != null && !requestDto.getHashtags().isEmpty()) {
            for (String tag : requestDto.getHashtags()) {
                HashTag hashTag = hashTagRepository.findByName(tag)
                        .orElseGet(() -> hashTagRepository.save(HashTag.builder().name(tag).build()));

                HashTagToPost hashTagToPost = HashTagToPost.builder()
                        .hashTag(hashTag)
                        .post(savedPost)
                        .build();

                hashTagToPostRepository.save(hashTagToPost);
                hashtags.add(tag);
            }
        }

        return PostResponseDto.of(savedPost.getId(), savedPost.getTitle(), savedPost.getContent(),
                savedPost.getImage(), savedPost.getNickname(), savedPost.getProfileImage(),
                savedPost.getAccountId(), savedPost.getLikeCount(), savedPost.getCommentCount(),
                false, hashtags, savedPost.getCreatedAt(), savedPost.getUpdatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long postId, Long accountId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        boolean isLiked = likeRepository.existsByPostIdAndAccountId(postId, accountId);

        List<String> hashtags = hashTagToPostRepository.findByPostId(postId).stream()
                .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                .collect(Collectors.toList());

        return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                post.getImage(), post.getNickname(), post.getProfileImage(),
                post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(Pageable pageable, Long accountId) {
        Page<Post> posts = postRepository.findAllActivePosts(pageable);

        return posts.map(post -> {
            boolean isLiked = likeRepository.existsByPostIdAndAccountId(post.getId(), accountId);

            List<String> hashtags = hashTagToPostRepository.findByPostId(post.getId()).stream()
                    .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                    .collect(Collectors.toList());

            return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                    post.getImage(), post.getNickname(), post.getProfileImage(),
                    post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                    isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByAccountId(Long accountId, Pageable pageable, Long currentAccountId) {
        Page<Post> posts = postRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);

        return posts.map(post -> {
            boolean isLiked = likeRepository.existsByPostIdAndAccountId(post.getId(), currentAccountId);

            List<String> hashtags = hashTagToPostRepository.findByPostId(post.getId()).stream()
                    .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                    .collect(Collectors.toList());

            return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                    post.getImage(), post.getNickname(), post.getProfileImage(),
                    post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                    isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getFeedByAccountId(Long accountId, Pageable pageable) {
        List<Long> followingAccountIds = new ArrayList<>();
        followingAccountIds.add(accountId);

        Page<Post> posts = postRepository.findFeedByFollowingAccountIds(followingAccountIds, pageable);

        return posts.map(post -> {
            boolean isLiked = likeRepository.existsByPostIdAndAccountId(post.getId(), accountId);

            List<String> hashtags = hashTagToPostRepository.findByPostId(post.getId()).stream()
                    .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                    .collect(Collectors.toList());

            return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                    post.getImage(), post.getNickname(), post.getProfileImage(),
                    post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                    isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPosts(String keyword, Pageable pageable, Long accountId) {
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);

        return posts.map(post -> {
            boolean isLiked = likeRepository.existsByPostIdAndAccountId(post.getId(), accountId);

            List<String> hashtags = hashTagToPostRepository.findByPostId(post.getId()).stream()
                    .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                    .collect(Collectors.toList());

            return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                    post.getImage(), post.getNickname(), post.getProfileImage(),
                    post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                    isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
        });
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, Long accountId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        if (!post.getAccountId().equals(accountId)) {
            throw new IllegalArgumentException("게시물을 수정할 권한이 없습니다.");
        }

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        if (requestDto.getImage() != null) {
            post.setImage(requestDto.getImage());
        }

        Post updatedPost = postRepository.save(post);

        hashTagToPostRepository.deleteByPostId(postId);

        List<String> hashtags = new ArrayList<>();
        if (requestDto.getHashtags() != null && !requestDto.getHashtags().isEmpty()) {
            for (String tag : requestDto.getHashtags()) {
                HashTag hashTag = hashTagRepository.findByName(tag)
                        .orElseGet(() -> hashTagRepository.save(HashTag.builder().name(tag).build()));

                HashTagToPost hashTagToPost = HashTagToPost.builder()
                        .hashTag(hashTag)
                        .post(updatedPost)
                        .build();

                hashTagToPostRepository.save(hashTagToPost);
                hashtags.add(tag);
            }
        }

        boolean isLiked = likeRepository.existsByPostIdAndAccountId(postId, accountId);

        return PostResponseDto.of(updatedPost.getId(), updatedPost.getTitle(), updatedPost.getContent(),
                updatedPost.getImage(), updatedPost.getNickname(), updatedPost.getProfileImage(),
                updatedPost.getAccountId(), updatedPost.getLikeCount(), updatedPost.getCommentCount(),
                isLiked, hashtags, updatedPost.getCreatedAt(), updatedPost.getUpdatedAt());
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long accountId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        if (!post.getAccountId().equals(accountId)) {
            throw new IllegalArgumentException("게시물을 삭제할 권한이 없습니다.");
        }

        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void likePost(Long postId, Long accountId, String nickname, String profileImage) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀다면 아무것도 하지 않음 (중복 방지)
        if (likeRepository.existsByPostIdAndAccountId(postId, accountId)) {
            log.info("이미 좋아요를 누른 게시물입니다. postId: {}, accountId: {}", postId, accountId);
            return;
        }

        Like like = Like.builder()
                .post(post)
                .accountId(accountId)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();

        likeRepository.save(like);
        post.incrementLikeCount();
        log.info("좋아요 추가 완료 - postId: {}, accountId: {}", postId, accountId);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, Long accountId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Optional<Like> likeOptional = likeRepository.findByPostIdAndAccountId(postId, accountId);
        
        // 좋아요를 누르지 않았다면 아무것도 하지 않음
        if (likeOptional.isEmpty()) {
            log.info("좋아요를 누르지 않은 게시물입니다. postId: {}, accountId: {}", postId, accountId);
            return;
        }

        likeRepository.delete(likeOptional.get());
        post.decrementLikeCount();
        postRepository.save(post);
        log.info("좋아요 취소 완료 - postId: {}, accountId: {}", postId, accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getTopLikedPosts(Long accountId) {
        List<Post> posts = postRepository.findTop10ByDeletedAtIsNullOrderByLikeCountDesc();

        return posts.stream().map(post -> {
            boolean isLiked = accountId != null && likeRepository.existsByPostIdAndAccountId(post.getId(), accountId);

            List<String> hashtags = hashTagToPostRepository.findByPostId(post.getId()).stream()
                    .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                    .collect(Collectors.toList());

            return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                    post.getImage(), post.getNickname(), post.getProfileImage(),
                    post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                    isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getRandomPosts(Long accountId) {
        log.info("getRandomPosts 호출 - accountId: {}", accountId);
        List<Post> posts = postRepository.findRandomPosts();

        return posts.stream()
                .map(post -> {
                    boolean isLiked = false;
                    if (accountId != null) {
                        isLiked = likeRepository.existsByPostIdAndAccountId(post.getId(), accountId);
                        log.info("좋아요 체크 - postId: {}, accountId: {}, existsByPostIdAndAccountId: {}", 
                            post.getId(), accountId, isLiked);
                    }
                    log.info("게시물 조회 - postId: {}, accountId: {}, isLiked: {}", post.getId(), accountId, isLiked);

                    List<String> hashtags = hashTagToPostRepository.findByPostId(post.getId()).stream()
                            .map(hashTagToPost -> hashTagToPost.getHashTag().getName())
                            .collect(Collectors.toList());

                    return PostResponseDto.of(post.getId(), post.getTitle(), post.getContent(),
                            post.getImage(), post.getNickname(), post.getProfileImage(),
                            post.getAccountId(), post.getLikeCount(), post.getCommentCount(),
                            isLiked, hashtags, post.getCreatedAt(), post.getUpdatedAt());
                })
                .collect(Collectors.toList());
    }
}
