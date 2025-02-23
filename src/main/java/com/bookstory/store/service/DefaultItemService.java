package com.bookstory.store.service;

import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultItemService implements ItemService {
    @Override
    public Optional<Item> createItem(Item item) {
        return Optional.empty();
    }

    @Override
    public List<Item> createItems(List<Item> item) {
        return List.of();
    }

    @Override
    public List<Item> getItemsByOrder(Order order) {
        return List.of();
    }
}
