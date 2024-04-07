package com.pessimistic.lock.order;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

import static com.pessimistic.lock.order.Status.EXECUTED;

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

    public void updateStatusToExecuted(List<Long> orderIds) {
        entityManager.unwrap(Session.class).createQuery("UPDATE Order SET status = :status WHERE id IN (:orderIds)")
            .setParameter("status", EXECUTED)
            .setParameter("orderIds", orderIds)
            .executeUpdate();
    }
}