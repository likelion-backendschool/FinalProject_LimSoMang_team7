package com.ll.exam.ebooks.app.order.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Order order;

    @ManyToOne(fetch = LAZY)
    private Product product;

    private LocalDateTime payDate; // 결제 날짜

    private int price; // 권장 판매가

    private int salePrice; // 실제 판매가

    private int wholesalePrice; // 도매가

    private int pgFee; // 결재대행사 수수료

    private int payPrice; // 결제 금액

    private int refundPrice; // 환불 금액

    private boolean isPaid; // 결제 여부

    public OrderItem(long id) {
        super(id);
    }


}
