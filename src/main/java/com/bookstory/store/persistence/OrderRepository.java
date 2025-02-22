package com.bookstory.store.persistence;

import com.bookstory.store.model.Item;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Item, Long> {
}
