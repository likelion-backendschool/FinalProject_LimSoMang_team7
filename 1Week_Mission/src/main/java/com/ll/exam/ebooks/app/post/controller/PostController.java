package com.ll.exam.ebooks.app.post.controller;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.post.dto.request.PostWriteRequestDto;
import com.ll.exam.ebooks.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final MemberService memberService;

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping("/write")
    public String showWrite() {
        return "/post/write";
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/write")
    public String write(@Valid PostWriteRequestDto writeDto, Principal principal) {
        String username = principal.getName();

        Member author = memberService.findByUsername(username).get();

        postService.save(author, writeDto.getSubject(), writeDto.getContent(), writeDto.getContentHtml());

        return "redirect:/";
    }
}
