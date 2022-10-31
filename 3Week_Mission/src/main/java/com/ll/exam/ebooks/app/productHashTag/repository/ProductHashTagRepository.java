package com.ll.exam.ebooks.app.productHashTag.repository;

import com.ll.exam.ebooks.app.productHashTag.entity.ProductHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductHashTagRepository extends JpaRepository<ProductHashTag, Long> {
    List<ProductHashTag> findAllByProductId(Long productId);

    Optional<ProductHashTag> findByProductIdAndProductKeywordId(Long productId, Long productKeywordId);
}
