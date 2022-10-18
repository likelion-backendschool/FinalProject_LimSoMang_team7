package com.ll.exam.ebooks.app.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping("/write")
    public String showWrite() {
        return "/post/write";
    }
}
