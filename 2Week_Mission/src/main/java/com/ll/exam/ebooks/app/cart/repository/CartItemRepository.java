package com.ll.exam.ebooks.app.cart.repository;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
