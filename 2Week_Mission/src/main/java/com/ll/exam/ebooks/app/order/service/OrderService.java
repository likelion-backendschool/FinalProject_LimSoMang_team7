package com.ll.exam.ebooks.app.order.service;

import com.ll.exam.ebooks.app.cart.entity.CartItem;
import com.ll.exam.ebooks.app.cart.service.CartService;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.order.entity.Order;
import com.ll.exam.ebooks.app.order.entity.OrderItem;
import com.ll.exam.ebooks.app.order.repository.OrderItemRepository;
import com.ll.exam.ebooks.app.order.repository.OrderRepository;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.sun.mail.imap.OlderTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

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

    @Transactional
    public Order createOrderByList(Member buyer, List<Product> products) {
        List<CartItem> cartItems = cartService.getItemsByBuyer(buyer.getId());

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

}
