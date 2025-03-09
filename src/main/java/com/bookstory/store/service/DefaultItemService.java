package com.bookstory.store.service;

import com.bookstory.store.model.Item;
import com.bookstory.store.persistence.ItemRepository;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultItemService implements ItemService {

    final public ItemRepository itemRepository;

    final public ItemMapper itemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ItemDTO> createItems(List<ItemDTO> itemDTOs) {
        log.info("create items");
        List<Item> items = itemDTOs.stream()
                .map(itemMapper::toEntity)
                .collect(Collectors.toList());
        List<Item> savedItems = itemRepository.saveAll(items);
        return savedItems.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
