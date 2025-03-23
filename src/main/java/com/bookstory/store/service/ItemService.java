package com.bookstory.store.service;

import com.bookstory.store.web.dto.ItemDTO;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

public interface ItemService {
    Flux<ItemDTO> createItems(@Valid Flux<ItemDTO> itemDTOs);
}
