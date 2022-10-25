package com.ll.exam.ebooks.app.cart.controller;

import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.cart.entity.CartItem;
import com.ll.exam.ebooks.app.cart.service.CartService;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{productId}")
    public String addCartItem(@PathVariable("productId") long id) {
        Member buyer = rq.getMember();
        Product product = productService.findById(id);

        CartItem cartItem = cartService.addCartItem(buyer, product);

        return "redirect:/member/profile";
    }
}
