package com.bookstory.store.persistence;

import com.bookstory.store.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Item, Long> {
}
