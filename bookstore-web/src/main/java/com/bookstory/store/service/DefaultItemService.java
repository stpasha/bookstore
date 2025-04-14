package com.bookstory.store.service;

import com.bookstory.store.repository.ItemRepository;
import com.bookstory.store.repository.ListItemRepository;
import com.bookstory.store.util.ObjectValidator;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultItemService implements ItemService {

    final public ItemRepository itemRepository;

    final public ListItemRepository listItemRepository;

    final public ItemMapper itemMapper;

    final private ObjectValidator objectValidator;

    @Override
    @Transactional
    @Secured("USER")
    public Flux<ItemDTO> createItems(Flux<ItemDTO> itemDTOs) {
        return itemRepository.saveAll(
                objectValidator.validate(itemDTOs).map(itemMapper::toEntity)
                        .doOnNext(entity -> log.info("Saving item entity: {}", entity))
        ).map(itemMapper::toDto);
    }

    @Secured("USER")
    @Override
    public Flux<ItemDTO> getItemsByOrderId(Mono<OrderDTO> orderDto) {
        return orderDto
                .flatMapMany(dto -> listItemRepository.findByOrdersId(dto.getId()))
                .map(itemMapper::toDto);
    }

}
