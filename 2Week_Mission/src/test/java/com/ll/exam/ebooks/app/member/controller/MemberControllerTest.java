package com.ll.exam.ebooks.app.member.controller;

import com.ll.exam.ebooks.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("회원가입 페이지")
    void showJoin() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/member/join"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showJoin"))
                .andExpect(content().string(containsString("회원가입")));
    }

    @Test
    @DisplayName("회원가입 로직")
    void join() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username", "user100")
                        .param("password", "12345678")
                        .param("passwordConfirm", "12345678")
                        .param("email", "user100@test.com")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrl("/member/login"));

        assertThat(memberService.findByUsername("user100")).isNotNull();
    }

    @Test
    @DisplayName("회원가입 로직")
    void joinFailByDuplicatedUsername() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/member/join")
                        .with(csrf())
                        .param("username", "user100")
                        .param("password", "12345678")
                        .param("passwordConfirm", "12345678")
                        .param("email", "user100@test.com")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(redirectedUrl("/member/login"));

        assertThat(memberService.findByUsername("user100")).isNotNull();
    }


    @Test
    @DisplayName("로그인 페이지")
    void showLogin() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/member/login"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showLogin"))
                .andExpect(content().string(containsString("로그인")));
    }

    @Test
    @DisplayName("아이디 찾기 페이지")
    void showFindUsername() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/member/findUsername"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showFindUsername"))
                .andExpect(content().string(containsString("아이디찾기")));
    }

    @Test
    @DisplayName("아이디 찾기 로직")
    void findUsername() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/member/findUsername")
                        .with(csrf())
                        .param("email", "user1@test.com")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findUsername"))
                .andExpect(content().string(containsString("user1")));

        assertThat(memberService.findByEmail("user1@test.com").getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("비밀번호 찾기 페이지")
    void showFindPassword() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(get("/member/findPassword"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showFindPassword"))
                .andExpect(content().string(containsString("비밀번호찾기")));
    }

    @Test
    @DisplayName("비밀번호 찾기 로직")
    void findPassword() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/member/findPassword")
                        .with(csrf())
                        .param("username", "user1")
                        .param("email", "user1@test.com")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("findPassword"))
                .andExpect(content().string("해당 이메일로 'user1' 계정의 임시 비밀번호를 발송했습니다."));

        assertThat(memberService.findByUsername("user1")).isNotNull();
    }

    @Test
    @WithUserDetails(value = "user1")
    @DisplayName("프로필 페이지")
    void showProfile() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/member/profile"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(content().string(containsString("프로필")));
    }

    @Test
    @WithUserDetails(value = "user1")
    @DisplayName("작가 등록 페이지")
    void showBeAuthor() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/member/beAuthor"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showBeAuthor"))
                .andExpect(content().string(containsString("작가 시작")));
    }

    @Test
    @WithUserDetails(value = "user4")
    @DisplayName("작가 등록 로직")
    void beAuthor() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/member/beAuthor")
                        .with(csrf())
                        .param("nickname", "도영")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("beAuthor"))
                .andExpect(redirectedUrl("/member/profile"));

        assertThat(memberService.findByUsername("user4").getNickname()).isEqualTo("도영");
    }

    @Test
    @WithUserDetails(value = "user1")
    @DisplayName("비밀번호 변경 페이지")
    void showModifyPassword() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/member/modifyPassword"))
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showModifyPassword"))
                .andExpect(content().string(containsString("비밀번호 변경")));
    }

    @Test
    @WithUserDetails(value = "user1")
    @DisplayName("비밀번호 변경 로직")
    void modifyPassword() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(post("/member/modifyPassword")
                        .with(csrf())
                        .param("username", "user1")
                        .param("oldPassword", "1234")
                        .param("password", "12345678")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("modifyPassword"))
                .andExpect(redirectedUrl("/member/profile"));

        assertThat(passwordEncoder.matches("12345678", memberService.findByUsername("user1").getPassword())).isTrue();
    }

}