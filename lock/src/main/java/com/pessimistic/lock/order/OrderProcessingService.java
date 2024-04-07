package com.pessimistic.lock.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

import static com.pessimistic.lock.order.Status.PENDING;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;

    public OrderProcessingService(OrderRepository oderRepository) {
        this.orderRepository = oderRepository;
    }

    @Transactional
    public void processOrders() {
        List<Order> pendingOrders = orderRepository.getAndLockOrderWithSkipLocked(PENDING, 10);
        orderRepository.updateStatusToExecuted(pendingOrders.stream().map(Order::getId).toList());
    }
}
