package com.ll.exam.ebooks.app.postKeyword.repository;

import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostKeywordRepository extends JpaRepository<PostKeyword, Long>, PostKeywordRepositoryCustom {
    Optional<PostKeyword> findByContent(String content);
}
