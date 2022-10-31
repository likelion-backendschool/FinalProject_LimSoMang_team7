package com.ll.exam.ebooks.app.post.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.form.PostForm;
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

    public Post findByIdAndPostHashTagFindByPostId(long id) {
        Post post = postRepository.findById(id).orElse(null);

        post.setHashTags(postHashTagService.findByPostId(id));

        return post;
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
        applyPostHashTags(post, postForm.getPostHashTags());

        return post;
    }

    // postHashTag 저장
    @Transactional
    public void applyPostHashTags(Post post, String postHashTags) {
        postHashTagService.apply(post, postHashTags);
    }

    @Transactional
    public void modify(Post post, PostForm postForm) {
        post.setSubject(postForm.getSubject());
        post.setContent(postForm.getContent());
        post.setContentHtml(postForm.getContentHtml());

        // 해시태그 적용
        String keywords = postForm.getPostHashTags();
        if (keywords != null) {
            postHashTagService.apply(post, keywords);
        }

        postRepository.save(post);
    }

    // post가 삭제되면, post + postKeyword 연결 끊기
    @Transactional
    public void remove(Post post) {
        postRepository.delete(post);

        if (post.getHashTags() != null) {
            postHashTagService.delete(post);
        }
    }

    // 글 수정 권한 여부 체크(권한: 글쓴이 본인)
    public boolean actorCanModify(Member actor, Post post) {
        return actor.getId().equals(post.getAuthor().getId());
    }

    // 글 삭제 권한 여부 체크(권한: 글쓴이 본인)
    public boolean actorCanRemove(Member actor, Post post) {
        return actorCanModify(actor, post);
    }

    // 글 조회 권한 여부 체크(권한: 글쓴이 본인)
    public boolean actorCanSee(Member actor, Post post) {
        return actorCanModify(actor, post);
    }

    public Post findBySubject(String subject) {
        return postRepository.findBySubject(subject).orElseThrow(() -> {
            throw new PostNotFoundException();
        });
    }
}
