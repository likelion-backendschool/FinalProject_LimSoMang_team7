package com.ll.exam.ebooks.app.productHashTag.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.productKeyword.entity.ProductKeyword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ProductHashTag extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    // DDMS 레벨에서 작동
    // DDL 생성시 cascade 제약 조건이 생성된다.
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne
    @ToString.Exclude
    private Member author;

    @ManyToOne
    @ToString.Exclude
    private ProductKeyword productKeyword;

    public String getSearchUrl() {
        String url = "/product/list?kwType=hashTag&kw=%s".formatted(this.getProductKeyword().getContent());
        return url;
    }
}
