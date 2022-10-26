package com.ll.exam.ebooks.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.ebooks.app.base.rq.Rq;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.order.entity.Order;
import com.ll.exam.ebooks.app.order.exception.ActorCanNotCancelOrderException;
import com.ll.exam.ebooks.app.order.exception.ActorCanNotPayOrderException;
import com.ll.exam.ebooks.app.order.exception.ActorCanNotSeeOrderException;
import com.ll.exam.ebooks.app.order.exception.OrderIdNotMatchedException;
import com.ll.exam.ebooks.app.order.exception.OrderNotEnoughRestCashException;
import com.ll.exam.ebooks.app.order.service.OrderService;
import com.ll.exam.ebooks.app.product.entity.Product;
import com.ll.exam.ebooks.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;
    private final MemberService memberService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final Rq rq;

    // 주문 상세 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String showDetail(@PathVariable long id, Model model) {
        Member actor = rq.getMember();
        Order order = orderService.findById(id);

        long restCash = memberService.getRestCash(actor);

        if (!orderService.actorCanSee(actor, order)) {
            throw new ActorCanNotSeeOrderException();
        }

        model.addAttribute("order", order);
        model.addAttribute("actorRestCash", restCash);

        return "order/detail";
    }

    // 내 주문 리스트 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {
        Member actor = rq.getMember();

        List<Order> orders = orderService.findAllByBuyerId(actor.getId());

        model.addAttribute("orders", orders);

        return "order/list";
    }

    // 주문 생성 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createOrder(@RequestParam(required = false) Long productId,
                              @RequestParam(required = false) String ids) {
        Member actor = rq.getMember();
        Order order = null;

        // 1. 단일 상품 주문인지 아닌지 확인한다.
        if (productId != null) {
            Product product = productService.findById(productId);
            order = orderService.createOrderByOne(actor, product);
        }

        // 2. 단일 주문이 아니라면, ids를 처리한다.
        if (ids != null) {
            String[] idsArr = ids.split(",");
            List<Product> products = new ArrayList<>();

            Arrays.stream(idsArr)
                    .mapToLong(Long::parseLong)
                    .forEach(id -> {
                        Product product = productService.findById(id);
                        products.add(product);
                    });

            order = orderService.createOrderByList(actor, products);
        }

        return "redirect:/order/%d".formatted(order.getId());
    }

    // 주문 취소 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable long id) {
        Member actor = rq.getMember();
        Order order = orderService.findById(id);

        if (!orderService.actorCanCancel(actor, order)) {
            throw new ActorCanNotCancelOrderException();
        }

        orderService.cancelOrder(order);

        return "redirect:/order/list";
    }

    // 결제 처리 구현
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/pay")
    public String payByOrder(@PathVariable long id) {
        Member actor = rq.getMember();
        Order order = orderService.findById(id);

        if (!orderService.actorCanPayment(actor, order)) {
            throw new ActorCanNotPayOrderException();
        }

        orderService.payByRestCashOnly(order);

        return "redirect:/order/%d".formatted(order.getId());
    }

    // Toss Payments 시작
    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        });
    }

    private final String SECRET_KEY = "test_sk_BE92LAa5PVbodgDpb0Rr7YmpXyJj";

    @RequestMapping("/{id}/success")
    public String confirmPayment(@PathVariable long id,
                                 @RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Long amount,
                                 Model model) throws Exception {
        Order order = orderService.findById(id);

        // 클라언트가 넘긴 orderId
        int orderIdInputed = Integer.parseInt(orderId.split("__")[1]);

        if (id != orderIdInputed) {
            throw new OrderIdNotMatchedException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        Member actor = rq.getMember();
        long restCash = memberService.getRestCash(actor);           // 보유 예치금
        long payPriceRestCash = order.calculatePayPrice() - amount; // 예치금 결제 금액 = 결제 금액 - pg 결제 금액

        // 예치금 결제 금액 > 보유 예치금 → 예치금 부족 예외 발생
        if (payPriceRestCash > restCash) {
            throw new OrderNotEnoughRestCashException();
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // Toss Payments 결제 처리
            orderService.payByTossPayments(order, payPriceRestCash);

            return "redirect:/order/%d".formatted(order.getId());
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());

            return "order/fail";
        }
    }

    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);

        return "order/fail";
    }

}
