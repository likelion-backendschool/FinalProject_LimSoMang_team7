package com.ll.exam.ebooks.app.base.initData;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import com.ll.exam.ebooks.app.cart.service.CartService;
import com.ll.exam.ebooks.app.member.form.JoinForm;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.order.entity.Order;
import com.ll.exam.ebooks.app.order.service.OrderService;
import com.ll.exam.ebooks.app.post.form.PostForm;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.form.ProductForm;
import com.ll.exam.ebooks.app.product.service.ProductService;

import java.util.List;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService, ProductService productService, CartService cartService, OrderService orderService) {
        JoinForm joinForm = new JoinForm("user1", "1234", "1234", "user1@test.com", "제노");
        Member member1 = memberService.adminJoin(joinForm);
        joinForm = new JoinForm("user2", "1234", "1234", "user2@test.com", "해찬");
        Member member2 = memberService.join(joinForm);
        joinForm = new JoinForm("user3", "1234", "1234", "user3@test.com", "마크");
        Member member3 = memberService.join(joinForm);
        joinForm = new JoinForm("user4", "1234", "1234", "user4@test.com", "");
        Member member4 = memberService.join(joinForm);

        PostForm postForm = new PostForm("test1", "## test1", "<h2>test1</h2>", "#test #user1");
        Post post1 = postService.write(member1, postForm);
        postForm = new PostForm("test2", "## test2", "<h2>test2</h2>", "#test #user1");
        Post post2 = postService.write(member1, postForm);
        postForm = new PostForm("test3", "## test3", "<h2>test3</h2>", "#test #user1");
        Post post3 = postService.write(member1, postForm);
        postForm = new PostForm("test4", "## test4", "<h2>test4</h2>", "#test #user2");
        Post post4 = postService.write(member2, postForm);
        postForm = new PostForm("test5", "## test5", "<h2>test5</h2>", "#test #user3");
        Post post5 = postService.write(member3, postForm);

        ProductForm productForm = new ProductForm("neo", 5000, "## sticker", "<h2>sticker</h2>", 1L, "#sticker #nct #neo");
        Product product1 = productService.create(member1, productForm);
        productForm = new ProductForm("mark", 2000, "## child", "<h2>child</h2>", 3L, "#child #mark #canada");
        Product product2 = productService.create(member2, productForm);
        productForm = new ProductForm("dream", 2000, "## we go up", "<h2>we go up</h2>", 1L, "#beatbox #dream #world");
        Product product3 = productService.create(member3, productForm);

        memberService.addCash(member1, 10_000, "충전__무통장입금");
        memberService.addCash(member2, 85_000, "충전__무통장입금");
        memberService.addCash(member3, 35_000, "충전__무통장입금");

        CartItem cartItem1 = cartService.addCartItem(member1, product1);
        CartItem cartItem2 = cartService.addCartItem(member2, product1);
        CartItem cartItem3 = cartService.addCartItem(member2, product2);
        CartItem cartItem4 = cartService.addCartItem(member2, product3);
        CartItem cartItem5 = cartService.addCartItem(member3, product3);

        Order order1 = orderService.createOrderByOne(member2, product1);
        Order order2 = orderService.createOrderByList(member2, List.of(product2, product3));

    }
}
