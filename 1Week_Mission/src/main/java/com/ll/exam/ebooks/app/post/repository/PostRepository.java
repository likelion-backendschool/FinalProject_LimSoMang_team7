package com.ll.exam.ebooks.app.post.repository;

import com.ll.exam.ebooks.app.post.dto.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
