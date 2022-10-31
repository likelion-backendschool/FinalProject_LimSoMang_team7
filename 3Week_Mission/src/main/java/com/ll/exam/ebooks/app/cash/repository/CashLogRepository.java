package com.ll.exam.ebooks.app.cash.repository;

import com.ll.exam.ebooks.app.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
