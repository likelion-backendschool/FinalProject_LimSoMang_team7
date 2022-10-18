package com.ll.exam.ebooks.app.member.controller;

import com.ll.exam.ebooks.app.member.dto.request.MemberJoinRequestDto;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        memberService.join(joinDto.getUsername(), joinDto.getPassword(), joinDto.getEmail(), joinDto.getNickname());

        return "redirect:/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

}
