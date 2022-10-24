package com.ll.exam.ebooks.app.cart.service;

import com.ll.exam.ebooks.app.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartService {
    private CartItemRepository cartItemRepository;
}
