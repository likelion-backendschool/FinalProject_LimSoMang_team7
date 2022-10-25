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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final Rq rq;


    // 내 장바구니 리스트
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {
        Member buyer = rq.getMember();

        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer.getId());

        model.addAttribute("cartItems", cartItems);

        return "/cart/list";
    }

    // 장바구니에 상품 추가 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{productId}")
    public String addCartItem(@PathVariable("productId") long id) {
        Member buyer = rq.getMember();
        Product product = productService.findById(id);

        CartItem cartItem = cartService.addCartItem(buyer, product);

        return "redirect:/cart/list";
    }

    // 장바구니에 있는 상품 삭제 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/remove/{productId}")
    public String removeCartItem(@PathVariable("productId") long id) {
        Member buyer = rq.getMember();
        Product product = productService.findById(id);

        cartService.removeCartItem(buyer, product);

        return "redirect:/cart/list";
    }
}
