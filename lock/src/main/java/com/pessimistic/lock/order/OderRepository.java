package com.pessimistic.lock.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
        SELECT * FROM `order` WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT 5 FOR UPDATE SKIP LOCKED;
    """, nativeQuery = true)
    List<Order> findByStatusOrderByCreatedAtAsc(Status status);
}
