package com.ll.exam.ebooks.app.postHashTag.repository;

import com.ll.exam.ebooks.app.postHashTag.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {
    Optional<PostHashTag> findByPostIdAndPostKeywordId(Long postId, Long postKeywordId);

    List<PostHashTag> findAllByPostId(Long postId);

    List<PostHashTag> findAllByAuthorIdAndPostKeywordIdOrderByPost_idDesc(Long authorId, Long postKeywordId);
}
