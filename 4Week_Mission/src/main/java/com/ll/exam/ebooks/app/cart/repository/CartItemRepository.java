package com.ll.exam.ebooks.app.cart.repository;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBuyerIdAndProductId(Long buyerId, Long productId);

    List<CartItem> findAllByBuyerId(Long id);
}
