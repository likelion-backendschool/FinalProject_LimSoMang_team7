package com.ll.exam.ebooks.app.member.controller;

import com.ll.exam.ebooks.app.member.dto.ModifyForm;
import com.ll.exam.ebooks.app.member.dto.JoinForm;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinDto) {
        memberService.join(joinDto.getUsername(), joinDto.getPassword(), joinDto.getEmail(), joinDto.getNickname());

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
    public String findUsername(@Valid ModifyForm modifyForm) {
        Member member = memberService.findByEmail(modifyForm.getEmail());

        return member.getUsername();
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findPassword")
    public String showFindPassword() {
        return "member/findPassword";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    @ResponseBody
    public String findPassword(@Valid ModifyForm modifyForm) {
        Member member = memberService.findByUsernameAndEmail(modifyForm.getUsername(), modifyForm.getEmail());

        memberService.setTempPassword(member);

        return "임시 비밀번호가 발급되었습니다. 이메일을 확인하세요.";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member member = memberContext.getMember();

        model.addAttribute("member", member);

        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        String username = memberContext.getName();

        Member member = memberService.findByUsername(username);

        model.addAttribute("member", member);

        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext memberContext, @Valid ModifyForm modifyForm) {
        memberService.modify(modifyForm.getUsername(), modifyForm.getEmail(), modifyForm.getNickname());

        Member member = memberService.findByUsername(memberContext.getUsername());

        memberContext.setModifyDate(member.getModifyDate());
        memberContext.setNickname(member.getNickname());
        memberContext.setEmail(member.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(memberContext, member.getPassword(), memberContext.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        String username = memberContext.getName();

        Member member = memberService.findByUsername(username);

        model.addAttribute("member", member);

        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(@AuthenticationPrincipal MemberContext memberContext, String password) {
        memberService.modifyPassword(memberContext.getUsername(), password);

        return "redirect:/member/profile";
    }
}
