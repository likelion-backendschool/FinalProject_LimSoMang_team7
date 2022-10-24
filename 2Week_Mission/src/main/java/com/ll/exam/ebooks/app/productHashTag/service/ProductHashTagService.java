package com.ll.exam.ebooks.app.productHashTag.service;

import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.productHashTag.entity.ProductHashTag;
import com.ll.exam.ebooks.app.productHashTag.repository.ProductHashTagRepository;
import com.ll.exam.ebooks.app.productKeyword.entity.ProductKeyword;
import com.ll.exam.ebooks.app.productKeyword.service.ProductKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductHashTagService {
    private final ProductHashTagRepository productHashTagRepository;
    private final ProductKeywordService productKeywordService;

    @Transactional
    public void apply(Product product, String productHashTags) {
        // 1. 기존 해시태그 가져오기
        List<ProductHashTag> oldProductTags = findByProductId(product.getId());

        // 2. 새로운 해시태그 키워드 리스트
        List<String> productKeywordContents = Arrays
                .stream(productHashTags.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        // 3. 삭제할 해시태그 구하기 (기존 해시태그 리스트에서 새로운 해시태그 리스트에 없는 것)
        List<ProductHashTag> deleteHashTags = new ArrayList<>();
        for (ProductHashTag oldHashTag : oldProductTags) {
            // 기존에 등록된 해시태그가 새롭게 등록된 해시태그에 포함되었는지 여부
            boolean contains = productKeywordContents
                    .stream()
                    .anyMatch(s -> s.equals(oldHashTag.getProductKeyword().getContent()));

            if (!contains) {
                deleteHashTags.add(oldHashTag);
            }
        }

        // 4. 3번에서 구한 해시태그 삭제
        deleteHashTags.forEach(hashTag -> {
            productHashTagRepository.delete(hashTag);
        });

        // 5. 나머지 해시태그 저장
        productKeywordContents.forEach(productKeywordContent -> {
            save(product, productKeywordContent);
        });
    }

    // 해시태그 저장
    private ProductHashTag save(Product product, String productKeywordContent) {
        // 1. keyword 저장 및 가져오기
        ProductKeyword productKeyword = productKeywordService.save(productKeywordContent);

        // 2. (productId + productKeywordId) 가 DB에 있으면 바로 리턴
        ProductHashTag hashTag = productHashTagRepository.findByProductIdAndProductKeywordId(product.getId(), productKeyword.getId()).orElse(null);

        if (hashTag != null) {
            return hashTag;
        }

        // 3. (productId + productKeywordId) 로 DB에 없으면 hashTag 저장
        hashTag = ProductHashTag
                .builder()
                .author(product.getAuthor())
                .product(product)
                .productKeyword(productKeyword)
                .build();

        productHashTagRepository.save(hashTag);

        return hashTag;
    }

    // productId로 hashTag 조회
    public List<ProductHashTag> findByProductId(Long productId) {
        return productHashTagRepository.findAllByProductId(productId);
    }
}
