package com.pessimistic.lock.order;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderProcessingService {

    @Async
    public void processOrders(List<Order> orders) {
        orders.forEach(Order::process);
    }
}
