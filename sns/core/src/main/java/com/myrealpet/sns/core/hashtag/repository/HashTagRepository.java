package com.myrealpet.sns.core.hashtag.repository;

import com.myrealpet.sns.core.hashtag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    
    Optional<HashTag> findByName(String name);
    
    List<HashTag> findByNameContaining(String keyword);
    
    boolean existsByName(String name);
}
