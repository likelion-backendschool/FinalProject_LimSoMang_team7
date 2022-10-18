package com.ll.exam.ebooks.app.member.controller;

import com.ll.exam.ebooks.app.member.dto.request.MemberFindRequestDto;
import com.ll.exam.ebooks.app.member.dto.request.MemberJoinRequestDto;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid MemberJoinRequestDto joinDto) {
        Member member = memberService.join(joinDto.getUsername(), joinDto.getPassword(), joinDto.getEmail(), joinDto.getNickname());

        String subject = "🦁멋북스 회원가입을 진심으로 환영합니다.";
        String message = "멋쟁이사자처럼, 책 많이 읽고 멋쟁이 되세요. 😎";

        memberService.mailSend(member, subject, message);

        return "redirect:/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String showFindUsername() {
        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    @ResponseBody
    public String findUsername(@Valid MemberFindRequestDto findDto) {
        Member member = memberService.findByEmail(findDto.getEmail());
        if (member != null) {
            return member.getUsername();
        } else {
            return "가입되지 않은 이메일입니다.";
        }
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "member/findPassword";
    }
}
