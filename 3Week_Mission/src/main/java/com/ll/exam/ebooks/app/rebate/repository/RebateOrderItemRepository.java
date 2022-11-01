package com.ll.exam.ebooks.app.rebate.repository;

import com.ll.exam.ebooks.app.rebate.entity.RebateOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RebateOrderItemRepository extends JpaRepository<RebateOrderItem, Long> {
    Optional<RebateOrderItem> findByOrderItemId(long orderItemId);
}
