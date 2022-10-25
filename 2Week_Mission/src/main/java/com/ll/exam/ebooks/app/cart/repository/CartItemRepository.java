package com.ll.exam.ebooks.app.cart.repository;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBuyerIdAndProductId(Long id, Long id1);
}
