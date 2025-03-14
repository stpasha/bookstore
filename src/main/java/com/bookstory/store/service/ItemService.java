package com.bookstory.store.service;

import com.bookstory.store.model.Order;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDTO> createItems(@Valid List<ItemDTO> item);
}
