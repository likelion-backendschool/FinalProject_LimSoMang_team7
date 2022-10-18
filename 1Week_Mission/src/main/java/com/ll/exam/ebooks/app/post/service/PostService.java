package com.ll.exam.ebooks.app.post.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public Post save(Member author, String subject, String content, String contentHtml) {
        Post post = Post
                .builder()
                .author(author)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        postRepository.save(post);

        return post;
    }

    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void modify(long id, String subject, String content, String contentHtml) {
        Post post = postRepository.findById(id).get();

        if (!post.getSubject().equals(subject)) {
            post.setSubject(subject);
        }

        if (!post.getContent().equals(content)) {
            post.setContent(content);
            post.setContentHtml(contentHtml);
        }

        postRepository.save(post);
    }
}
