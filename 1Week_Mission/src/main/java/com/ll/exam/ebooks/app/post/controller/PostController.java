package com.ll.exam.ebooks.app.post.controller;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.dto.PostForm;
import com.ll.exam.ebooks.app.post.dto.ResponsePost;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;
import com.ll.exam.ebooks.app.security.dto.MemberContext;
import com.ll.exam.ebooks.app.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;


    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public String showList(Model model) {
        List<ResponsePost> posts = postService.list();

        model.addAttribute("posts", posts);

        return "post/list";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public String showDetail(@PathVariable long id, Model model) {
        ResponsePost post = postService.findOne(id);

        model.addAttribute("post", post);

        return "post/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite(@AuthenticationPrincipal MemberContext memberContext) {
        if (memberContext.getNickname().trim().length() > 0) {
            return "/post/write";
        }

        return "redirect:/?msg=" + Ut.url.encode("마이페이지에서 닉네임을 입력해주세요.");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@Valid PostForm postDto, @AuthenticationPrincipal MemberContext memberContext) {
        Member author = memberContext.getMember();

        log.debug("postDto : " + postDto);

        postService.write(author, postDto.getSubject(), postDto.getContent(), postDto.getContentHtml(), postDto.getTags());

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, Model model) {
        Post post = postService.findById(id);

        model.addAttribute("post", post);

        return "/post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable long id, @Valid PostForm postDto) {
        postService.modify(id, postDto.getSubject(), postDto.getContent(), postDto.getContentHtml());

        return "redirect:/";
    }
}
