package com.ll.exam.ebooks.app.post.controller;

import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.form.PostForm;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.base.exception.ActorCanNotModifyException;
import com.ll.exam.ebooks.app.post.service.PostService;
import com.ll.exam.ebooks.app.postHashTag.service.PostHashTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final PostHashTagService postHashTagService;
    private final Rq rq;


    // 글 작성폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        Member member = rq.getMember();
        log.debug("member : " + member);
        if (member.getNickname() == null || member.getNickname().equals("")) {
            return "redirect:/member/beAuthor";
        }

        return "post/write";
    }

    // 글 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@Valid PostForm postForm) {
        Member author = rq.getMember();

        Post post = postService.write(author, postForm);

        return "redirect:/post/%d".formatted(post.getId());
    }

    // 내 글 상세 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(@PathVariable long id, Model model) {
        Member member = rq.getMember();
        Post post = postService.findById(id);

        post.setHashTags(postHashTagService.findByPostId(id));

        // 조회 권한 검사
        if (postService.actorCanSee(member, post) == false) {
            throw new ActorCanNotModifyException();
        }

        model.addAttribute("post", post);

        return "post/detail";
    }

    // 내 글 리스트 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(@RequestParam(defaultValue = "hashTag") String kwType,
                           @RequestParam(defaultValue = "") String kw,
                           Model model) {
        Member author = rq.getMember();

        List<Post> posts = postService.search(author, kwType, kw);

        model.addAttribute("posts", posts);

        return "post/list";
    }

    // 글 수정폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, Model model) {
        Member member = rq.getMember();
        Post post = postService.findById(id);

        // 수정 권한 검사
        if (!postService.actorCanModify(member, post)) {
            throw new ActorCanNotModifyException();
        }
        model.addAttribute("post", post);

        return "post/modify";
    }

    // 글 수정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable long id,
                         @Valid PostForm postForm) {
        Member member = rq.getMember();
        Post post = postService.findById(id);

        // 수정 권한 검사
        if (!postService.actorCanModify(member, post)) {
            throw new ActorCanNotModifyException();
        }
        postService.modify(post, postForm);

        return "redirect:/post/%d".formatted(post.getId());
    }

    // 글 삭제 구현
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        Member member = rq.getMember();
        Post post = postService.findById(id);

        if (!postService.actorCanDelete(member, post)) {
            throw new ActorCanNotModifyException();
        }
        postService.delete(post);

        return "redirect:/";
    }
}
