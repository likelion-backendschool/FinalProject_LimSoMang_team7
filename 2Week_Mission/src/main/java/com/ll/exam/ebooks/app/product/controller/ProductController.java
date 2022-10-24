package com.ll.exam.ebooks.app.product.controller;

import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.exception.ActorCanNotModifyException;
import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.ll.exam.ebooks.app.postKeyword.service.PostKeywordService;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.form.ProductForm;
import com.ll.exam.ebooks.app.product.form.ProductModifyForm;
import com.ll.exam.ebooks.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final PostKeywordService postKeywordService;
    private final Rq rq;

    // 상품 작성 폼
    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @GetMapping("/create")
    public String showCreate(Model model) {
        List<PostKeyword> postKeywords = postKeywordService.findByMemberId(rq.getId());

        model.addAttribute("postKeywords", postKeywords);

        return "product/create";
    }

    // 상품 작성 구현
    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @PostMapping("/create")
    public String create(@Valid ProductForm productForm) {
        Member author = rq.getMember();

        Product product = productService.create(author, productForm);

        return "redirect:/product/" + product.getId();
    }

    // 상품 상세조회
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        List<Post> posts = productService.findPostsByProduct(product);

        model.addAttribute("product", product);
        model.addAttribute("posts", posts);

        return "product/detail";
    }

    // 리스트
    @GetMapping("/list")
    public String list(Model model) {
        List<Product> products = productService.findAllByOrderByIdDesc();

        model.addAttribute("products", products);

        return "product/list";
    }

    // 상품 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, Model model) {
        Product product = productService.findById(id);

        Member actor = rq.getMember();

        if (!productService.actorCanModify(actor, product)) {
//            return "redirect:/product/%s".formatted(id);
            throw new ActorCanNotModifyException();
        }

        model.addAttribute("product", product);

        return "product/modify";
    }

    // 상품 수정 로직 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable long id, @Valid ProductModifyForm productModifyForm) {
        Product product = productService.findById(id);

        Member actor = rq.getMember();

        if (!productService.actorCanModify(actor, product)) {
            throw new ActorCanNotModifyException();
        }

        productService.modify(product, productModifyForm);

        return "redirect:/product/" + product.getId();
    }
}
