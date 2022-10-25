package com.ll.exam.ebooks.app.cart.service;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import com.ll.exam.ebooks.app.cart.repository.CartItemRepository;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItem addCartItem(Member buyer, Product product) {
        // 1. 상품이 장바구니에 담겨 있는지 확인한다.
        CartItem oldCartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId()).orElse(null);

        // 2. 이미 담겨있으면, 담겨있는 상품을 리턴한다.
        if (oldCartItem != null) {
            return oldCartItem;
        }

        CartItem cartItem = CartItem
                .builder()
                .buyer(buyer)
                .product(product)
                .build();

        cartItemRepository.save(cartItem);

        return cartItem;
    }
}
