package com.ll.exam.ebooks.app.post.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.dto.PostForm;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.exception.PostNotFoundException;
import com.ll.exam.ebooks.app.post.repository.PostRepository;
import com.ll.exam.ebooks.app.postHashTag.service.PostHashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostHashTagService postHashTagService;

    public Post findById(long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new PostNotFoundException();
        });
    }

    // 전체 글 리스트 조회
    public List<Post> mainList(String kwType, String kw) {
        return postRepository.mainListSearchQsl(kwType, kw);
    }

    // 내 글 전체 조회
    public List<Post> findAllByAuthorIdOrderByIdDesc(Long authorId) {
        return postRepository.findAllByAuthorIdOrderByIdDesc(authorId);
    }

    // 내 글 전체/키워드 기반 검색 조회
    public List<Post> search(Member author, String kwType, String kw) {
        return postRepository.searchQsl(author, kwType, kw);
    }


    @Transactional
    public Post write(Member author, PostForm postForm) {
        Post post = Post
                .builder()
                .subject(postForm.getSubject())
                .content(postForm.getContent())
                .contentHtml(postForm.getContentHtml())
                .author(author)
                .build();

        postRepository.save(post);

        // hashTag 적용
        String keywords = postForm.getHashTags();
        if (keywords != null) {
            postHashTagService.apply(post, keywords);
        }

        return post;
    }

    @Transactional
    public void modify(Post post, PostForm postForm) {
        post.setSubject(postForm.getSubject());
        post.setContent(postForm.getContent());
        post.setContentHtml(postForm.getContentHtml());

        // 해시태그 적용
        String keywords = postForm.getHashTags();
        if (keywords != null) {
            postHashTagService.apply(post, keywords);
        }

        postRepository.save(post);
    }

    @Transactional
    public void delete(Post post) {
        postRepository.delete(post);

        if (post.getHashTags() != null) {
            postHashTagService.delete(post);
        }
    }

    // 글 수정 권한 여부 체크(권한: 글쓴이 본인)
    public boolean canModify(Member member, Post post) {
        return member.getId().equals(post.getAuthor().getId());
    }

    // 글 삭제 권한 여부 체크(권한: 글쓴이 본인)
    public boolean canDelete(Member member, Post post) {
        return canModify(member, post);
    }

    // 글 조회 권한 여부 체크(권한: 글쓴이 본인)
    public boolean canSelect(Member member, Post post) {
        return canModify(member, post);
    }


}
