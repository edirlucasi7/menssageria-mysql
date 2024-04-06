package com.pessimistic.lock.order;

import java.math.BigDecimal;

public record OrderRequest(String name, BigDecimal amount) {

    public Order toModel() {
        return new Order(name, amount);
    }
}