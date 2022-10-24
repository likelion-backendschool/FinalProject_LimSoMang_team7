package com.ll.exam.ebooks.app.product.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
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
public class Product extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Member author;

    @ManyToOne(fetch = LAZY)
    private PostKeyword postKeyword;

    private String subject;

    private int price;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "LONGTEXT")
    private String contentHtml;

    public Product(long id) {
        super(id);
    }
}
