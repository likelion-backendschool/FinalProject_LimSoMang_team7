package com.ll.exam.ebooks.app.api.controller;

import com.ll.exam.ebooks.app.base.dto.RsData;
import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.form.LoginForm;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.myBook.entity.MyBook;
import com.ll.exam.ebooks.app.myBook.service.MyBookService;
import com.ll.exam.ebooks.app.security.dto.MemberContext;
import com.ll.exam.ebooks.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ApiController {

    private final MemberService memberService;
    private final MyBookService myBookService;

    @PostMapping("/member/login")
    public ResponseEntity<RsData> login(@RequestBody LoginForm loginForm) {
        Member member = memberService.findByUsername(loginForm.getUsername());

        if (member == null) {
            return Ut.spring.responseEntityOf(RsData.of("F-1", "일치하는 회원이 존재하지 않습니다."));
        }

        String accessToken = memberService.genAccessToken(member);

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

    @GetMapping("/member/me")
    public ResponseEntity<RsData> me(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext == null) { // 임시코드, 나중에는 시프링 시큐리티를 이용해서 로그인을 안했다면, 아예 여기로 못 들어오도록
            return Ut.spring.responseEntityOf(RsData.failOf(null));
        }

        return Ut.spring.responseEntityOf(RsData.successOf(memberContext.getMember()));
    }

    @GetMapping("/myBooks")
    public ResponseEntity<RsData> myBooks(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();

        log.debug("member: " + member.getId());

        if (member == null) {
            return Ut.spring.responseEntityOf(
                    RsData.of(
                            "F-1",
                            "조회 권한이 없습니다."
                    )
            );
        }

        List<MyBook> myBooks = myBookService.findAllByOwnerId(member.getId());

        log.debug("myBooks: " + myBooks);

        return Ut.spring.responseEntityOf(
                RsData.successOf(
                        Ut.mapOf(
                                "myBooks", myBooks
                        )
                )
        );
    }

}
