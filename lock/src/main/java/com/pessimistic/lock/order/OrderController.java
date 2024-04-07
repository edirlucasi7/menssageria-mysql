package com.pessimistic.lock.order;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderProcessingService orderProcessingService;
    private final EntityManager entityManager;

    public OrderController(OrderRepository orderRepository, OrderProcessingService orderProcessingService, EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.orderProcessingService = orderProcessingService;
        this.entityManager = entityManager;
    }

    @Transactional
    @PostMapping("/api/order/create")
    public ResponseEntity<Void> create(@RequestBody OrderRequest request) {
        Order order = request.toModel();
        entityManager.persist(order);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/order/process")
    public ResponseEntity<Void> process() {
        Thread thread1 = new Thread(orderProcessingService::processOrders);
        Thread thread2 = new Thread(orderProcessingService::processOrders);

        thread1.start();
        thread2.start();

        return ResponseEntity.ok().build();
    }
}
