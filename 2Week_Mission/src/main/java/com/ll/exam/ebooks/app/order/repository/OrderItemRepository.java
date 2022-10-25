package com.ll.exam.ebooks.app.order.repository;

import com.ll.exam.ebooks.app.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
