package com.ll.exam.ebooks.app.order.service;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import com.ll.exam.ebooks.app.cart.service.CartService;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.myBook.service.MyBookService;
import com.ll.exam.ebooks.app.order.entity.Order;
import com.ll.exam.ebooks.app.order.entity.OrderItem;
import com.ll.exam.ebooks.app.order.exception.ActorCanNotPayOrderException;
import com.ll.exam.ebooks.app.order.repository.OrderItemRepository;
import com.ll.exam.ebooks.app.order.repository.OrderRepository;
import com.ll.exam.ebooks.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final MemberService memberService;
    private final MyBookService myBookService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 단일 상품 생성 로직
    @Transactional
    public Order createOrderByOne(Member buyer, Product product) {
        CartItem cartItem = cartService.getItemByBuyerAndProduct(buyer, product);

        OrderItem orderItem = new OrderItem(product);
        
        cartService.removeCartItem(cartItem);

        return createByOne(buyer, orderItem);
    }

    @Transactional
    public Order createByOne(Member buyer, OrderItem orderItem) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .build();

        order.addOrderItem(orderItem);

        // 주문 품목으로부터 이름을 만든다.
        order.makeName();

        // 주문 완료 처리
        order.setReadyStatusDone();
        orderRepository.save(order);

        return order;
    }

    // 다중 상품 생성 로직
    @Transactional
    public Order createOrderByList(Member buyer, List<Product> products) {
        List<CartItem> cartItems = new ArrayList<>();

        products.forEach(product -> {
            CartItem cartItem = cartService.getItemByBuyerAndProduct(buyer, product);
            cartItems.add(cartItem);
        });

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            // 주문 가능하면 주문에 상품 추가
            if (product.isOrderable()) {
                orderItems.add(new OrderItem(product));
            }
            // 장바구니에서 상품 삭제
            cartService.removeCartItem(buyer, product);
        }

        return createByList(buyer, orderItems);
    }

    @Transactional
    public Order createByList(Member buyer, List<OrderItem> orderItems) {
        Order order = Order
                .builder()
                .buyer(buyer)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 품목으로부터 이름을 만든다.
        order.makeName();

        // 주문 완료 처리
        order.setReadyStatusDone();
        orderRepository.save(order);

        return order;
    }

    // 주문 취소 로직
    @Transactional
    public void cancelOrder(Order order) {
        order.setCancelDate();

        orderItemRepository.deleteAllByOrderId(order.getId());

        orderRepository.delete(order);
    }

    // 예치금으로 전액 결제
    @Transactional
    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();            // 구매자
        long restCash = buyer.getRestCash();        // 예치금 잔액
        int payPrice = order.calculatePayPrice();   // 결제 금액

        // 예치금이 결제 금액보다 적으면, 결제 거절
        if (payPrice > restCash) {
            throw new ActorCanNotPayOrderException("예치금이 부족합니다.");
        }

        // 예치금 결제 처리
        memberService.addCash(buyer, payPrice * -1, "주문__%d__사용__예치금".formatted(order.getId()));

        // 결제 완료 처리
        order.setPaymentDone();
        orderRepository.save(order);

        addPaidMyBooks(buyer, order);
    }

    // 결제완료된 상품을 내 도서에 추가
    @Transactional
    public void addPaidMyBooks(Member buyer, Order order) {
        // 단일 상품
        if (order.getOrderItems().size() == 1) {
            OrderItem orderItem = getOrderItemByOrderId(order.getId());

            myBookService.addPaidBook(buyer, orderItem.getProduct());
        }

        // 다중 상품
        else {
            List<OrderItem> orderItems = getOrderItemsByOrderId(order.getId());

            for (OrderItem orderItem : orderItems) {
                myBookService.addPaidBook(buyer, orderItem.getProduct());
            }
        }
    }

    // TossPayments 결제 (전액 결재 or 혼합 결제)
    @Transactional
    public void payByTossPayments(Order order, long useRestCash) {
        Member buyer = order.getBuyer();            // 구매자
        int payPrice = order.calculatePayPrice();   // 결제 금액
        long pgPayPrice = payPrice - useRestCash;   // 카드 결제 금액

        // 카드 결제 예치금 처리
        memberService.addCash(buyer, pgPayPrice, "주문__%d__충전__토스페이먼츠".formatted(order.getId()));
        memberService.addCash(buyer, pgPayPrice * -1, "주문__%d__사용__토스페이먼츠".formatted(order.getId()));

        // 사용된 예치금 처리
        if (useRestCash > 0) {
            memberService.addCash(buyer, useRestCash * -1, "주문__%d__사용__예치금".formatted(order.getId()));
        }

        // 결제 완료 처리
        order.setPaymentDone();
        orderRepository.save(order);

        addPaidMyBooks(buyer, order);
    }

    // 결제 상품 환불하기
    @Transactional
    public void refund(Order order) {
        order.setCancelDone();

        int payPrice = order.getPayPrice();
        memberService.addCash(order.getBuyer(), payPrice, "주문__%d__환불__예치금".formatted(order.getId()));

        order.setRefundDone();
        orderRepository.save(order);

        myBookService.remove(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> findAllByBuyerId(Long id) {
        return orderRepository.findAllByBuyerId(id);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long id) {
        return orderItemRepository.findAllByOrderId(id);
    }

    private OrderItem getOrderItemByOrderId(Long id) {
        return orderItemRepository.findByOrderId(id);
    }

    public boolean actorCanSee(Member actor, Order order) {
        return actor.getId().equals(order.getBuyer().getId());
    }

    public boolean actorCanPayment(Member actor, Order order) {
        return actorCanSee(actor, order);
    }

    public boolean actorCanCancel(Member actor, Order order) {
        return actorCanSee(actor, order);
    }

    public boolean actorCanRefund(Member actor, Order order) {
        // 볼 수 있는 권한이 있는지
        if (!actorCanSee(actor, order)) {
            return false;
        };

        // 환불 가능한 상태인지
        if (!order.isRefundable()) {
            return false;
        }

        // 환불 가능한 시간대인지 구하기(10분 이내)
        LocalDateTime payTime = order.getCreateDate();
        LocalDateTime refundTime = LocalDateTime.now();

        long between = ChronoUnit.MINUTES.between(payTime, refundTime);

        if (between > 10) {
            return false;
        }

        return true;
    }
}
