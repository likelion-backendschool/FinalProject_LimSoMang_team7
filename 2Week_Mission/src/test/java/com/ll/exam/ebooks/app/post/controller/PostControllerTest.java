package com.ll.exam.ebooks.app.post.controller;

import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithUserDetails(value = "user2")
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("글 작성 페이지")
    void showWrite() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/post/write"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showWrite"))
                .andExpect(content().string(containsString("글 작성")));
    }

    @Test
    @WithUserDetails("user4")
    @DisplayName("글 작성 페이지 - 권한이 없어서 작가명 등록 페이지로 이동")
    void showWriteFailByAuthorization() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/post/write"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showWrite"))
                .andExpect(redirectedUrl("/member/beAuthor"));
    }

    @Test
    void write() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/post/write")
                        .with(csrf())
                        .param("subject", "post_subject")
                        .param("content", "## post_content")
                        .param("contentHtml", "<h2>post_content</h2>")
                        .param("postHashTags", "#post #subject #content #user2")
                )
                .andDo(print());

        Post post = postService.findBySubject("post_subject");

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(redirectedUrl("/post/%d".formatted(post.getId())));
    }

    @Test
    @DisplayName("글 상세조회 페이지")
    void showDetail() throws Exception {
        // Given
        long id = 4;

        // When
        ResultActions resultActions = mvc
                .perform(get("/post/%d".formatted(id)))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showDetail"))
                .andExpect(content().string(containsString("상세화면")));
    }


    @Test
    @DisplayName("내 글 리스트 조회")
    void showList() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/post/list"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(content().string(containsString("글 목록")));

    }

    @Test
    @DisplayName("글 수정 페이지")
    void showModify() throws Exception {
        // Given
        long id = 4;

        // When
        ResultActions resultActions = mvc
                .perform(get("/post/%d/modify".formatted(id)))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("showModify"))
                .andExpect(content().string(containsString("글 수정")));
    }

    @Test
    @DisplayName("글 수정 로직")
    void modify() throws Exception {
        // Given
        long id = 4;

        // When
        ResultActions resultActions = mvc
                .perform(post("/post/%d/modify".formatted(id))
                        .with(csrf())
                        .param("subject", "post_subject")
                        .param("content", "## post_content")
                        .param("contentHtml", "<h2>post_content</h2>")
                        .param("postHashTags", "#post #subject #content #user2")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(redirectedUrl("/post/%d".formatted(id)));


    }

    @Test
    @DisplayName("글 삭제 로직")
    void remove() throws Exception {
        // Given
        long id = 4;

        // When
        ResultActions resultActions = mvc
                .perform(post("/post/%d/remove".formatted(id)))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(PostController.class))
                .andExpect(handler().methodName("remove"));
    }
}