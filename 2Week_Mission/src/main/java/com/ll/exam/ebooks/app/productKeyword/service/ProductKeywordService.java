package com.ll.exam.ebooks.app.productKeyword.service;

import com.ll.exam.ebooks.app.productKeyword.entity.ProductKeyword;
import com.ll.exam.ebooks.app.productKeyword.repository.ProductKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductKeywordService {
    private final ProductKeywordRepository productKeywordRepository;

    // 키워드 저장
    @Transactional
    public ProductKeyword save(String content) {
        Optional<ProductKeyword> optionalProductKeyword = productKeywordRepository.findByContent(content);

        // 1. 해당 키워드가(content)가 DB에 있으면 바로 리턴
        if (optionalProductKeyword.isPresent()) {
            return optionalProductKeyword.get();
        }

        // 2. 해당 키워드(content)가 DB에 없으면 저장
        ProductKeyword productKeyword = ProductKeyword
                .builder()
                .content(content)
                .build();

        productKeywordRepository.save(productKeyword);

        return productKeyword;
    }
}
