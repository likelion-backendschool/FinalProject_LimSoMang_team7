package com.ll.exam.ebooks.app.myBook.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.order.entity.OrderItem;
import com.ll.exam.ebooks.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
public class MyBook extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Member owner;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Product product;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private OrderItem orderItem;

    public MyBook(long id) {
        super(id);
    }
}
