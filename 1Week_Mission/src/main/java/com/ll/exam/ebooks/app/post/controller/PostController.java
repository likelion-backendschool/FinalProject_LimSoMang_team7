package com.ll.exam.ebooks.app.post.controller;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.post.dto.request.PostRequestDto;
import com.ll.exam.ebooks.app.post.dto.response.ResponsePost;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final MemberService memberService;


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

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping("/write")
    public String showWrite() {
        return "/post/write";
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/write")
    public String write(@Valid PostRequestDto postDto, Principal principal) {
        String username = principal.getName();

        Member author = memberService.findByUsername(username).get();

        postService.save(author, postDto.getSubject(), postDto.getContent(), postDto.getContentHtml());

        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, Model model) {
        Post post = postService.findById(id).get();

        model.addAttribute("post", post);

        return "/post/modify";
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable long id, @Valid PostRequestDto postDto) {
        postService.modify(id, postDto.getSubject(), postDto.getContent(), postDto.getContentHtml());

        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable long id, Principal principal) {
        String username = principal.getName();

        Member author = memberService.findByUsername(username).get();

        boolean isDelete = postService.delete(author, id);

        if (isDelete) {
            return "redirect:/post/list";
        } else {
            return "redirect:/post/" + id;
        }
    }
}
