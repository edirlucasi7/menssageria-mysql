package com.pessimistic.lock.order;

import org.hibernate.LockOptions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@Repository
public class OrderRepository {

    private final EntityManager entityManager;

    public OrderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Order> getAndLockOrderWithSkipLocked(Status status, int orderCount) {
        String query = """
                    SELECT o FROM Order o WHERE o.status = :status ORDER BY o.id
                """;

        return entityManager
                .createQuery(query, Order.class)
                .setParameter("status", status)
                .setMaxResults(orderCount)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setHint("javax.persistence.lock.timeout", LockOptions.SKIP_LOCKED)
                .getResultList();
    }
}