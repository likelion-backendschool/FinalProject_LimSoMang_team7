package com.ll.exam.ebooks.app.post.repository;

import com.ll.exam.ebooks.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    List<Post> findAllByAuthorIdOrderByIdDesc(long authorId);

    Optional<Post> findBySubject(String subject);
}
