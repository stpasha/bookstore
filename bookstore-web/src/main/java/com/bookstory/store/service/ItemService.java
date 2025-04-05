package com.bookstory.store.service;

import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {
    Flux<ItemDTO> createItems(Flux<ItemDTO> itemDTOs);

    Flux<ItemDTO> getItemsByOrderId(Mono<OrderDTO> orderDto);
}
