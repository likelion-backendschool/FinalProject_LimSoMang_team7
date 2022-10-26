package com.ll.exam.ebooks.app.myBook.service;

import com.ll.exam.ebooks.app.myBook.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyBookService {
    private final MyBookRepository myBookRepository;
}
