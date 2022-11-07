package com.ll.exam.ebooks.app.home;

import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PostService postService;

    @GetMapping("/")
    public String showMain(@RequestParam(defaultValue = "hashTag") String kwType,
                           @RequestParam(defaultValue = "") String kw,
                           Model model) {
        List<Post> posts = postService.mainList(kwType, kw);

        model.addAttribute("posts", posts);

        return "home/main";
    }
}
