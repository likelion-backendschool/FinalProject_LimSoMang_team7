package com.ll.exam.ebooks.app.order.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

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
public class Order extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    private Member buyer;

    private LocalDateTime payDate; // 결제 날짜

    private boolean readyStatus; // 주문 완료 여부

    private boolean isPaid; // 결제 완료 여부

    private boolean isCanceled; // 취소 여부

    private boolean isReturned; // 환불 여부

    private String name; // 주문명

    public Order(long id) {
        super(id);
    }

}
