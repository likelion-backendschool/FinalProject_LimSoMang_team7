package com.ll.exam.ebooks.app.cash.service;

import com.ll.exam.ebooks.app.cash.repository.CashLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CashLogService {
    private final CashLogRepository cashLogRepository;
}
