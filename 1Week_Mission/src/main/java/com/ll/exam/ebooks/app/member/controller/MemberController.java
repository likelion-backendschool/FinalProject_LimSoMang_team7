package com.ll.exam.ebooks.app.member.controller;

import com.ll.exam.ebooks.app.member.dto.request.MemberFindRequestDto;
import com.ll.exam.ebooks.app.member.dto.request.MemberJoinRequestDto;
import com.ll.exam.ebooks.app.member.dto.request.MemberModifyProfileRequestDto;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

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
        Member member = memberService.findByEmail(findDto.getEmail()).get();

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

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findPassword")
    @ResponseBody
    public String findPassword(@Valid MemberFindRequestDto findDto) {
        Member member = memberService.findByUsernameAndEmail(findDto.getUsername(), findDto.getEmail()).get();

        if (member != null) {
            UUID uuid = UUID.randomUUID();
            String tempPassword = uuid.toString().substring(0, 6);
            memberService.setTempPassword(member, tempPassword);

            String subject = "🦁멋북스 " + member.getUsername() + "의 임시 비밀번호가 발급되었습니다.";
            String message = member.getUsername() + "의 임시 비밀번호는 " + tempPassword + " 입니다.😎";

            memberService.mailSend(member, subject, message);

            return "임시 비밀번호가 발급되었습니다. 이메일을 확인하세요.";
        } else {
            return "입력하신 정보가 올바르지 않습니다.";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        String username = principal.getName();

        Member member = memberService.findByUsername(username).get();

        model.addAttribute("member", member);

        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify(Principal principal, Model model) {
        String username = principal.getName();

        Member member = memberService.findByUsername(username).get();

        model.addAttribute("member", member);

        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@Valid MemberModifyProfileRequestDto modifyDto) {
        memberService.modify(modifyDto.getUsername(), modifyDto.getEmail(), modifyDto.getNickname());

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword(Principal principal, Model model) {
        String username = principal.getName();

        Member member = memberService.findByUsername(username).get();

        model.addAttribute("member", member);

        return "member/modifyPassword";
    }
}
