package com.ll.exam.ebooks.app.post.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.component.PostComponent;
import com.ll.exam.ebooks.app.post.dto.response.ResponsePost;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostComponent postComponent;

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

    @Transactional(readOnly = true)
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

    @Transactional
    public List<ResponsePost> list() {
        List<ResponsePost> posts = postRepository.findAll()
                .stream()
                .map(p -> getResponsePost(p))
                .collect(Collectors.toList());

        return posts;
    }

    @Transactional
    public ResponsePost findOne(long id) {
        Post post = postRepository.findById(id).get();

        ResponsePost responsePost = getResponsePost(post);

        return responsePost;
    }

    private ResponsePost getResponsePost(Post post) {
        return postComponent.getResponsePost(post);
    }

    public boolean delete(Member author, long id) {
        Post post = postRepository.findById(id).get();

        if (author.getUsername() == post.getAuthor().getUsername()) {
            postRepository.delete(post);
            return true;
        }

        return false;
    }
}
