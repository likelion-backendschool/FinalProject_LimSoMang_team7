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
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        orderRepository.save(order);

        return order;
    }

    // 주문 취소 로직
    @Transactional
    public void cancelOrder(Order order) {
        orderItemRepository.deleteAllByOrderId(order.getId());

        orderRepository.delete(order);
    }

    // 전액 예치금 결제
    @Transactional
    public void payByRestCashOnly(Order order) {
        Member buyer = order.getBuyer();

        // 예치금
        long restCash = buyer.getRestCash();

        // 결제 금액
        int payPrice = order.calculatePayPrice();

        // 예치금이 결제 금액보다 적을 때
        if (payPrice > restCash) {
            throw new ActorCanNotPayOrderException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, "%d번__상품결제".formatted(order.getId()));

        order.setPaymentDone();
        orderRepository.save(order);

        addPaidMyBooks(buyer, order);
    }

    @Transactional
    public void addPaidMyBooks(Member buyer, Order order) {
        if (order.getOrderItems().size() == 1) {
            OrderItem orderItem = getOrderItemByOrderId(order.getId());

            myBookService.addPaidBook(buyer, orderItem.getProduct());
        }

        else {
            List<OrderItem> orderItems = getOrderItemsByOrderId(order.getId());

            for (OrderItem orderItem : orderItems) {
                // 결제 완료된 상품을 내 도서에 추가
                myBookService.addPaidBook(buyer, orderItem.getProduct());
            }
        }
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

}
