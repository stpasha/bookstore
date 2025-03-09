package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDTO> createItems(List<ItemDTO> item);
}
