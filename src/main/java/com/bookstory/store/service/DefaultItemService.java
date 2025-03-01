package com.bookstory.store.service;

import com.bookstory.store.model.Item;
import com.bookstory.store.persistence.ItemRepository;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.dto.OrderDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultItemService implements ItemService {

    final public ItemRepository itemRepository;

    final public ItemMapper itemMapper;

    @Override
    @Transactional
    public Optional<ItemDTO> createItem(ItemDTO itemDTO) {
        try {
            log.info("create item {}", itemDTO);
            Item item = itemMapper.toEntity(itemDTO);
            Item savedItem = itemRepository.save(item);
            log.info("item created id {}", savedItem.getId());
            return Optional.of(itemMapper.toDto(savedItem));
        } catch (Exception e) {
            log.error("Unexpected error while creating item", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional
    public List<ItemDTO> createItems(List<ItemDTO> itemDTOs) {
        try {
            log.info("create items");
            List<Item> items = itemDTOs.stream()
                    .map(itemMapper::toEntity)
                    .collect(Collectors.toList());
            List<Item> savedItems = itemRepository.saveAll(items);
            return savedItems.stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Unexpected error while creating item", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<ItemDTO> getItemsByOrder(OrderDTO order) {
        log.info("get items for order id {}", order.getId());
        Optional<List<Item>> items = itemRepository.findByOrderId(order.getId());
        return items.map(itemMapper::toDtoList).orElseGet(ArrayList::new);
    }
}
