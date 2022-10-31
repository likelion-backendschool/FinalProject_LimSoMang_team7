package com.ll.exam.ebooks.app.myBook.repository;

import com.ll.exam.ebooks.app.myBook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    void deleteByProductIdAndOwnerId(Long productId, Long ownerId);
}
