package com.bookstory.store.tests.service;

import com.bookstory.store.annotations.StoreTestAnnotation;
import com.bookstory.store.model.Item;
import com.bookstory.store.persistence.ItemRepository;
import com.bookstory.store.persistence.ProductRepository;
import com.bookstory.store.service.DefaultItemService;
import com.bookstory.store.util.TestDataFactory;
import com.bookstory.store.web.dto.ItemDTO;
import com.bookstory.store.web.mapper.ItemMapper;
import com.bookstory.store.web.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@StoreTestAnnotation
public class ItemServiceTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private DefaultItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void createItems() {
        List<ItemDTO> itemDTOs = testDataFactory.createItemDTOs(3);
        List<Item> items = itemDTOs.stream().map(itemMapper::toEntity).toList();

        itemDTOs.forEach(itemDTO -> {
            when(productRepository.findById(itemDTO.getProduct().getId()))
                    .thenReturn(Optional.of(productMapper.toEntity(itemDTO.getProduct())));
        });

        when(itemRepository.saveAll(items)).thenReturn(items);

        List<ItemDTO> result = itemService.createItems(itemDTOs);

        assertEquals(itemDTOs.size(), result.size());

        verify(itemRepository, times(1)).saveAll(anyList());
    }

}
