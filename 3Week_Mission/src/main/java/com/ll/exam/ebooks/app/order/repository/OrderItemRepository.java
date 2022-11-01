package com.ll.exam.ebooks.app.order.repository;

import com.ll.exam.ebooks.app.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteAllByOrderId(Long id);

    List<OrderItem> findAllByOrderId(Long id);

    OrderItem findByOrderId(Long id);

    List<OrderItem> findAllByPayDateBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
