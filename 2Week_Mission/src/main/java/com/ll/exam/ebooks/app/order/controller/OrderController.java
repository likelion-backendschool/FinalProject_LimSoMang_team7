package com.ll.exam.ebooks.app.order.controller;

import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.order.entity.Order;
import com.ll.exam.ebooks.app.order.entity.OrderItem;
import com.ll.exam.ebooks.app.order.service.OrderService;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.service.ProductService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;
    private final MemberService memberService;
    private final Rq rq;

    // 주문 상세 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(@PathVariable long id, Model model) {
        Member buyer = rq.getMember();
        Order order = orderService.findById(id);

        long restCash = memberService.getRestCash(buyer);

        model.addAttribute("order", order);
        model.addAttribute("actorRestCash", restCash);

        return "order/detail";
    }

    // 내 주문 리스트 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {
        Member buyer = rq.getMember();

        List<Order> orders = orderService.findAllByBuyerId(buyer.getId());

        model.addAttribute("orders", orders);

        return "order/list";
    }

    // 주문 생성 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createOrder(@RequestParam(required = false) Long productId,
                              @RequestParam(required = false) String ids) {
        Member buyer = rq.getMember();
        Order order = null;

        // 1. 단일 상품 주문인지 아닌지 확인한다.
        if (productId != null) {
            Product product = productService.findById(productId);
            order = orderService.createOrderByOne(buyer, product);
        }

        // 2. 단일 주문이 아니라면, ids를 처리한다.
        if (ids != null) {
            String[] idsArr = ids.split(",");
            List<Product> products = new ArrayList<>();

            Arrays.stream(idsArr)
                    .mapToLong(Long::parseLong)
                    .forEach(id -> {
                        Product product = productService.findById(id);
                        products.add(product);
                    });

            order = orderService.createOrderByList(buyer, products);
        }

        return "redirect:/order/%d".formatted(order.getId());
    }

    // 결제 처리 구현


    // 주문 취소 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable long id) {
        Member buyer = rq.getMember();
        Order order = orderService.findById(id);

        orderService.cancelOrder(order);

        return "redirect:/order/list";
    }

}
