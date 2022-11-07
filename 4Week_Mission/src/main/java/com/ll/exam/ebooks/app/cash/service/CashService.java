package com.ll.exam.ebooks.app.cash.service;

import com.ll.exam.ebooks.app.cash.entity.CashLog;
import com.ll.exam.ebooks.app.cash.repository.CashLogRepository;
import com.ll.exam.ebooks.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CashService {
    private final CashLogRepository cashLogRepository;

    @Transactional
    public CashLog addCash(Member member, long price, String eventType) {
        CashLog cashLog = CashLog
                .builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
}
