package com.ll.exam.ebooks.app.post.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.component.PostComponent;
import com.ll.exam.ebooks.app.post.dto.ResponsePost;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.repository.PostRepository;
import com.ll.exam.ebooks.app.postKeyword.service.PostKeywordService;
import com.ll.exam.ebooks.app.postTag.service.PostTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostComponent postComponent;
    private final PostTagService postTagService;

    @Transactional
    public Post write(Member author, String subject, String content, String contentHtml, String postTags) {
        Post post = Post
                .builder()
                .author(author)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        postRepository.save(post);

        applyPostTags(post, postTags);

        return post;
    }

    public void applyPostTags(Post post, String postTags) {
        postTagService.applyPostTags(post, postTags);
    }

    @Transactional(readOnly = true)
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("글이 존재하지 않습니다."));
    }

    @Transactional
    public void modify(long id, String subject, String content, String contentHtml) {
        Post post = findById(id);

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

    @Transactional(readOnly = true)
    public ResponsePost findOne(long id) {
        Post post = findById(id);

        ResponsePost responsePost = getResponsePost(post);

        return responsePost;
    }

    private ResponsePost getResponsePost(Post post) {
        return postComponent.getResponsePost(post);
    }
}
