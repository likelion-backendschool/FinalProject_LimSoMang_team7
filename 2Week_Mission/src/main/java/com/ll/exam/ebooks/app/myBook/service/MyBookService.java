package com.ll.exam.ebooks.app.myBook.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.myBook.entity.MyBook;
import com.ll.exam.ebooks.app.myBook.repository.MyBookRepository;
import com.ll.exam.ebooks.app.order.entity.OrderItem;
import com.ll.exam.ebooks.app.order.service.OrderService;
import com.ll.exam.ebooks.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MyBookService {
    private final MyBookRepository myBookRepository;

    public void addPaidBook(Member member, Product product) {
        MyBook myBook = MyBook
                .builder()
                .member(member)
                .product(product)
                .build();

        myBookRepository.save(myBook);
    }
}
