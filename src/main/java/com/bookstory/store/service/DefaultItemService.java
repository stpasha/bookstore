package com.bookstory.store.service;

import com.bookstory.store.repository.ItemRepository;
import com.bookstory.store.repository.ListItemRepository;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class DefaultItemService implements ItemService {

    final public ItemRepository itemRepository;

    final public ListItemRepository listItemRepository;

    final public ItemMapper itemMapper;

    @Override
    @Transactional
    public Flux<ItemDTO> createItems(@Valid Flux<ItemDTO> itemDTOs) {
        return itemRepository.saveAll(
                itemDTOs.map(itemMapper::toEntity)
                        .doOnNext(entity -> log.info("Saving item entity: {}", entity))
        ).map(itemMapper::toDto);
    }

    @Override
    public Flux<ItemDTO> getItemsByOrderId(Mono<OrderDTO> orderDto) {
        return orderDto
                .flatMapMany(dto -> listItemRepository.findByOrdersId(dto.getId()))
                .map(itemMapper::toDto);
    }

}
