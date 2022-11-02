package com.ll.exam.ebooks.app.post.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.repository.MemberRepository;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.form.PostForm;
import com.ll.exam.ebooks.app.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("전체 글 리스트 조회")
    void mainList() {
        // Given
        String kwType = "hashTag";
        String kw = "user1";

        // When
        List<Post> mainList = postService.mainList(kwType, kw);

        // Then
       assertThat(mainList.size()).isGreaterThan(0);
       assertThat(mainList.get(0).getAuthor().getNickname()).isEqualTo("제노");
       assertThat(mainList.get(0).getHashTagString().contains("user1")).isTrue();
    }

    @Test
    @WithUserDetails("user2")
    @DisplayName("내 글 전체 기반 검색 조회")
    void search() {
        // Given
        Member author = memberRepository.findById(2L).orElse(null);
        String kwType = "hashTag";
        String kw = "";

        // When
        List<Post> searchList = postService.search(author, kwType, kw);

        // Then
        assertThat(searchList.size()).isGreaterThan(0);
        assertThat(searchList.get(0).getAuthor()).isEqualTo(author);
        assertThat(searchList.get(0).getSubject()).isNotNull();
        assertThat(searchList.get(0).getContent()).isNotNull();
    }

    @Test
    @WithUserDetails("user2")
    @DisplayName("내 글 키워드 기반 검색 조회")
    void searchByKw() {
        // Given
        Member author = memberRepository.findById(2L).orElse(null);
        String kwType = "hashTag";
        String kw = "user2";

        // When
        List<Post> searchList = postService.search(author, kwType, kw);

        // Then
        assertThat(searchList.size()).isGreaterThan(0);
        assertThat(searchList.get(0).getAuthor()).isEqualTo(author);
        assertThat(searchList.get(0).getSubject()).isNotNull();
        assertThat(searchList.get(0).getContent()).isNotNull();
        assertThat(searchList.get(0).getHashTagString().contains("user2")).isTrue();
    }

    @Test
    @WithUserDetails("user2")
    @DisplayName("글 작성")
    void write() {
        // Given
        Member author = memberRepository.findById(2L).orElse(null);
        PostForm postForm = PostForm
                .builder()
                .subject("test100")
                .content("## test100")
                .contentHtml("<h2>test100</h2>")
                .postHashTags("#test100 #user2")
                .build();

        // When
        Post _post = postService.write(author, postForm);
        Post post = postService.findBySubject("test100");

        // Then
        assertEquals(post.getAuthor(), author);
        assertEquals(post.getSubject(), postForm.getSubject());
        assertEquals(post.getContent(), postForm.getContent());
        assertEquals(post.getContentHtml(), postForm.getContentHtml());
    }

    @Test
    @WithUserDetails("user2")
    @DisplayName("글 수정")
    void applyPostHashTags() {
        // Given
        Post _post = postService.findById(3L);

        PostForm postForm = PostForm
                .builder()
                .subject("test1000")
                .content("## test1000")
                .contentHtml("<h2>test1000</h2>")
                .postHashTags("#test1000 #user2")
                .build();

        // When
        postService.modify(_post, postForm);
        Post post = postService.findById(3L);
        System.out.println("post: " + post);

        // Then
        assertEquals(post.getSubject(), postForm.getSubject());
        assertEquals(post.getContent(), postForm.getContent());
        assertEquals(post.getContentHtml(), postForm.getContentHtml());
        assertEquals(post.getHashTagString(), postForm.getPostHashTags());
    }

    @Test
    @DisplayName("글 삭제 + postKeyword 연결 끊기")
    void remove() {
        // Given
        Post _post = postService.findById(3L);

        // When
        postService.remove(_post);
        Post post = postRepository.findById(3L).orElse(null);

        // Then
        assertThat(post).isNull();
    }

}