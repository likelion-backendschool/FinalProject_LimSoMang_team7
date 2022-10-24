package com.ll.exam.ebooks.app.product.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.postHashTag.entity.PostHashTag;
import com.ll.exam.ebooks.app.postHashTag.service.PostHashTagService;
import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.ll.exam.ebooks.app.postKeyword.service.PostKeywordService;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.form.ProductForm;
import com.ll.exam.ebooks.app.product.form.ProductModifyForm;
import com.ll.exam.ebooks.app.product.repository.ProductRepository;
import com.ll.exam.ebooks.app.productHashTag.service.ProductHashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final PostKeywordService postKeywordService;
    private final ProductHashTagService productHashTagService;
    private final PostHashTagService postHashTagService;

    // postKeywordId로 postKeyword 찾기
    @Transactional
    public Product create(Member author, ProductForm productForm) {
        PostKeyword postKeyword = postKeywordService.findById(productForm.getPostKeywordId()).get();

        return create(author, productForm.getSubject(), productForm.getPrice(), productForm.getContent(), productForm.getContentHtml(), postKeyword, productForm.getProductHashTags());
    }

    // product 생성
    @Transactional
    public Product create(Member author, String subject, int price, String content, String contentHtml, PostKeyword postKeyword, String productHashTags) {
        Product product = Product
                .builder()
                .author(author)
                .postKeyword(postKeyword)
                .subject(subject)
                .price(price)
                .content(content)
                .contentHtml(contentHtml)
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

    // 상품 수정
    @Transactional
    public void modify(Product product, ProductModifyForm productModifyForm) {
        product.setSubject(productModifyForm.getSubject());
        product.setContent(productModifyForm.getContent());
        product.setContentHtml(productModifyForm.getContentHtml());

        applyProductHashTags(product, productModifyForm.getProductHashTags());
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // 상품에 포함된 글 리스트 조회
    public List<Post> findPostsByProduct(Product product) {
        Member author = product.getAuthor();
        PostKeyword postKeyword = product.getPostKeyword();
        List<PostHashTag> postHashTags = postHashTagService.getPostTags(author.getId(), postKeyword.getId());

        return postHashTags
                .stream()
                .map(PostHashTag::getPost)
                .collect(Collectors.toList());
    }

    // 전체 상품 리스트 조회
    public List<Product> findAllByOrderByIdDesc() {
        return productRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public void delete(Product product) {
        productRepository.delete(product);

        if (product.getHashTags() != null) {
            deleteProductHashTag(product);
        }
    }

    @Transactional
    public void deleteProductHashTag(Product product) {
        productHashTagService.delete(product);
    }

    // 상품 수정 권한 여부 체크(권한: 작가 본인)
    public boolean actorCanModify(Member actor, Product product) {
        if (actor == null) return false;

        return actor.getId().equals(product.getAuthor().getId());
    }
    // 상품 삭제 권한 여부 체크(권한: 작가 본인)

    public boolean actorCanDelete(Member actor, Product product) {
        return actorCanModify(actor, product);
    }
}
