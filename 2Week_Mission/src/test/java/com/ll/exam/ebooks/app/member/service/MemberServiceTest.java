package com.ll.exam.ebooks.app.member.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.form.JoinForm;
import com.ll.exam.ebooks.app.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("일반 회원가입")
    void join() {
        // Given
        JoinForm joinForm = JoinForm
                .builder()
                .username("user100")
                .password("12345678")
                .passwordConfirm("12345678")
                .email("user100@test.com")
                .nickname("user100_nickname")
                .build();

        // When
        Member member = memberService.join(joinForm);

        // Then
        assertThat(member.getId()).isGreaterThan(0);
        assertThat(joinForm.getPassword()).isNotNull();
        assertEquals(member.getUsername(), joinForm.getUsername());
        assertEquals(member.getEmail(), joinForm.getEmail());
        assertEquals(member.getNickname(), joinForm.getNickname());
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("관리자 회원가입")
    void adminJoin() {
        // Given
        JoinForm joinForm = JoinForm
                .builder()
                .username("user100")
                .password("12345678")
                .passwordConfirm("12345678")
                .email("user100@test.com")
                .nickname("user100_nickname")
                .build();

        // When
        Member member = memberService.adminJoin(joinForm);

        // Then
        assertThat(member.getId()).isGreaterThan(0);
        assertThat(joinForm.getPassword()).isNotNull();
        assertEquals(member.getUsername(), joinForm.getUsername());
        assertEquals(member.getEmail(), joinForm.getEmail());
        assertEquals(member.getNickname(), joinForm.getNickname());
        assertEquals(member.getAuthLevel(), 7);
    }

    @Test
    @DisplayName("이메일로 Member 찾기")
    void findByEmail() {
        // Given
        String email = "user2@test.com";

        // When
        Member member = memberService.findByEmail(email);

        // Then
        assertThat(member.getId()).isGreaterThan(0);
        assertEquals(member.getUsername(), "user2");
        assertEquals(member.getEmail(), email);
        assertEquals(member.getNickname(), "해찬");
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("아이디와 이메일로 Member 찾기")
    void findByUsernameAndEmail() {
        // Given
        String username = "user2";
        String email = "user2@test.com";

        // When
        Member member = memberService.findByUsernameAndEmail(username, email);

        // Then
        assertThat(member.getId()).isGreaterThan(0);
        assertEquals(member.getUsername(), username);
        assertEquals(member.getEmail(), email);
        assertEquals(member.getNickname(), "해찬");
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("닉네임(작가명)으로 Member 찾기")
    void findByUsername() {
        // Given
        String username = "user2";

        // Then
        Member member = memberService.findByUsername(username);

        // When
        assertThat(member.getId()).isGreaterThan(0);
        assertEquals(member.getUsername(), username);
        assertEquals(member.getEmail(), "user2@test.com");
        assertEquals(member.getNickname(), "해찬");
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("닉네임(작가명)으로 Member 찾기")
    void findByNickname() {
        // Given
        String nickname = "해찬";

        // When
        Member member = memberService.findByNickname(nickname);

        // Then
        assertThat(member.getId()).isGreaterThan(0);
        assertEquals(member.getUsername(), "user2");
        assertEquals(member.getEmail(), "user2@test.com");
        assertEquals(member.getNickname(), nickname);
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("임시 비밀번호 설정하기")
    void setTempPassword() {
        // Given
        String username = "user2";
        Member _member = memberRepository.findByUsername(username).get();

        // Then
        memberService.setTempPassword(_member);

        Member member = memberRepository.findByUsername(username).get();

        // When
        assertThat(member.getId()).isGreaterThan(0);
        assertThat(passwordEncoder.matches(member.getPassword(), _member.getPassword())).isFalse();
        assertEquals(member.getUsername(), username);
        assertEquals(member.getEmail(), "user2@test.com");
        assertEquals(member.getNickname(), "해찬");
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("비밀번호 변경하기")
    void modifyPassword() {
        // Given
        Member _member = memberRepository.findById(2L).get();
        String oldPassword = "1234";
        String password = "12341234";

        // When
        boolean isModify = memberService.modifyPassword(_member, oldPassword, password);

        Member member = memberRepository.findById(2L).get();

        // Then
        assertThat(isModify).isTrue();
        assertThat(member.getId()).isGreaterThan(0);
        assertThat(passwordEncoder.matches(member.getPassword(), _member.getPassword())).isFalse();
        assertEquals(member.getUsername(), "user2");
        assertEquals(member.getEmail(), "user2@test.com");
        assertEquals(member.getNickname(), "해찬");
        assertEquals(member.getAuthLevel(), 3);
    }

    @Test
    @DisplayName("닉네임(작가명) 설정하기")
    void beAuthor() {
        // Given
        Member _member = memberRepository.findById(4L).get();
        String nickname = "런쥔";

        // When
        boolean isAuthor = memberService.beAuthor(_member, nickname);

        Member member = memberRepository.findById(4L).get();

        // Then
        assertThat(isAuthor).isTrue();
        assertThat(member.getId()).isGreaterThan(0);
        assertEquals(member.getUsername(), "user4");
        assertEquals(member.getEmail(), "user4@test.com");
        assertEquals(member.getNickname(), "런쥔");
        assertEquals(member.getAuthLevel(), 3);
    }
}