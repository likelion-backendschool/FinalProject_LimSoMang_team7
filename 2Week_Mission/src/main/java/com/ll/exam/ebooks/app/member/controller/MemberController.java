package com.ll.exam.ebooks.app.member.controller;

import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.member.form.JoinForm;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private final Rq rq;

    // 로그인 폼
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin() {
        return "member/login";
    }

    // 회원가입 폼
    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin(JoinForm joinForm) {
        return "member/join";
    }

    // 회원가입
    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, BindingResult bindingResult) {
        // 아이디 중복 검사
        Member oldMember = memberService.findByUsername(joinForm.getUsername());
        if (oldMember != null) {
            bindingResult.rejectValue("username", "duplicated username", "중복된 아이디입니다.");
            return "member/join";
        }

        if (!joinForm.getPassword().equals(joinForm.getPasswordConfirm())) {
            bindingResult.rejectValue("password", "no matched password", "비밀번호가 일치하지 않습니다.");
            return "member/join";
        }

        // 이메일 중복 검사
        oldMember = memberService.findByEmail(joinForm.getEmail());
        if (oldMember != null) {
            bindingResult.rejectValue("email", "duplicated email", "중복된 이메일입니다.");
            return "member/join";
        }

        memberService.join(joinForm);

        return "/member/login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/findUsername")
    public String showFindUsername() {
        return "member/findUsername";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/findUsername")
    @ResponseBody
    public String findUsername(String email) {
        Member member = memberService.findByEmail(email);

        if (member == null) {
            return "일치하는 회원이 존재하지 않습니다.";
        }

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
    public String findPassword(String username, String email) {
        Member member = memberService.findByUsernameAndEmail(username, email);

        if (member == null) {
            return "일치하는 회원이 존재하지 않습니다.";
        }

        memberService.setTempPassword(member);

        return "해당 이메일로 '%s' 계정의 임시 비밀번호를 발송했습니다.".formatted(member.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(Model model) {
        Member member = rq.getMember();

        model.addAttribute("member", member);

        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifyPassword")
    public String showModifyPassword(Model model) {
        Member member = rq.getMember();

        model.addAttribute("member", member);

        return "member/modifyPassword";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifyPassword")
    public String modifyPassword(String oldPassword, String password) {
        Member member = rq.getMember();
        boolean isModify = memberService.modifyPassword(member, oldPassword, password);

        if (!isModify) {
            return rq.historyBack("비밀번호 변경에 실패했습니다.");
        }

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/beAuthor")
    public String showBeAuthor() {
        return "member/beAuthor";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/beAuthor")
    public String beAuthor(String nickname) {
        Member member = rq.getMember();

        boolean isAuthor = memberService.beAuthor(member, nickname);

        if (!isAuthor) {
            return "redirect:/member/beAuthor?errorMsg=" + Ut.url.encode("이미 사용 중인 닉네임입니다.");
        }

        return "redirect:/member/profile";
    }
}
