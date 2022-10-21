package com.ll.exam.ebooks.app.postTag.repository;

import com.ll.exam.ebooks.app.postTag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    Optional<PostTag> findByPostIdAndPostKeywordId(Long postId, Long postKeywordId);
}
