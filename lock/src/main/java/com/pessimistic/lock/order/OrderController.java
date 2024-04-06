package com.pessimistic.lock.order;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static com.pessimistic.lock.order.Status.PENDING;

@RestController
public class OrderController {

    private final OderRepository oderRepository;
    private final OrderRepository2 orderRepository2;
    private final OrderProcessingService orderProcessingService;
    private final EntityManagerFactory entityManagerFactory;

    public OrderController(OderRepository oderRepository, OrderRepository2 orderRepository2, OrderProcessingService orderProcessingService, EntityManagerFactory entityManagerFactory) {
        this.orderRepository2 = orderRepository2;
        this.oderRepository = oderRepository;
        this.orderProcessingService = orderProcessingService;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Transactional
    @PostMapping("/api/order/create")
    public ResponseEntity<Void> create(@RequestBody OrderRequest request) {
        Order order = request.toModel();
        oderRepository.save(order);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/order/process")
    public ResponseEntity<Void> process() {
        Thread thread1 = new Thread(() -> doInJPA(_entityManager -> {
            List<Order> _first = orderRepository2.getAndLockOrderWithSkipLocked(_entityManager, PENDING, 10);
            System.out.println(_first);
            orderProcessingService.processOrders(_first);
        }));

        Thread thread2 = new Thread(() -> doInJPA(_entityManager -> {
            List<Order> first = orderRepository2.getAndLockOrderWithSkipLocked(_entityManager, PENDING, 10);
            System.out.println(first);
            orderProcessingService.processOrders(first);
        }));

        thread1.start();
        thread2.start();

        return ResponseEntity.ok().build();
    }

    public void doInJPA(Consumer<EntityManager> action) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            action.accept(entityManager);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
