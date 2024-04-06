package com.pessimistic.lock.order;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import static com.pessimistic.lock.order.Status.EXECUTED;
import static com.pessimistic.lock.order.Status.PENDING;
import static java.time.LocalDateTime.*;

@Entity
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status = PENDING;

    private LocalDateTime createdAt = now();

    @Deprecated
    public Order() {
    }

    public Order(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void process() {
        this.status = EXECUTED;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}