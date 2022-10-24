package com.ll.exam.ebooks.app.product.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.ll.exam.ebooks.app.postKeyword.service.PostKeywordService;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.form.ProductForm;
import com.ll.exam.ebooks.app.product.repository.ProductRepository;
import com.ll.exam.ebooks.app.productHashTag.service.ProductHashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final PostKeywordService postKeywordService;
    private final ProductHashTagService productHashTagService;

    // postKeywordId로 postKeyword 찾기
    @Transactional
    public Product create(Member author, ProductForm productForm) {
        PostKeyword postKeyword = postKeywordService.findById(productForm.getPostKeywordId()).get();

        return create(author, productForm.getSubject(), productForm.getPrice(), postKeyword, productForm.getProductHashTags());
    }

    // product 생성
    @Transactional
    public Product create(Member author, String subject, int price, PostKeyword postKeyword, String productHashTags) {
        Product product = Product
                .builder()
                .author(author)
                .postKeyword(postKeyword)
                .subject(subject)
                .price(price)
                .build();

        productRepository.save(product);

        applyProductHashTags(product, productHashTags);

        return product;
    }

    // productHashTag 저장
    @Transactional
    public void applyProductHashTags(Product product, String productHashTags) {
        productHashTagService.apply(product, productHashTags);
    }
}
