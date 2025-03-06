package com.bookstory.store.persistence;

import com.bookstory.store.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findAll();
}
