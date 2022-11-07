package com.ll.exam.ebooks.app.api.controller;

import com.ll.exam.ebooks.app.base.dto.RsData;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.form.LoginForm;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiController {

    private final MemberService memberService;

    @PostMapping("/member/login")
    public ResponseEntity<RsData> login(@RequestBody LoginForm loginForm) {
        Member member = memberService.findByUsername(loginForm.getUsername());

        if (member == null) {
            return Ut.spring.responseEntityOf(RsData.of("F-1", "일치하는 회원이 존재하지 않습니다."));
        }

        String accessToken = "JWT_Access_Token";

        return Ut.spring.responseEntityOf(
                RsData.of(
                        "S-1",
                        "로그인 성공, Access Token을 발급합니다.",
                        Ut.mapOf(
                                "accessToken", accessToken
                        )
                ),
                Ut.spring.httpHeadersOf("Authentication", accessToken)
        );
    }

}
