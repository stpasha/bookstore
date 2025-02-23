package com.bookstory.store.service;

import com.bookstory.store.model.Item;
import com.bookstory.store.model.Order;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<Item> createItem(Item item);
    List<Item> createItems(List<Item> item);
    List<Item> getItemsByOrder(Order order);

}
