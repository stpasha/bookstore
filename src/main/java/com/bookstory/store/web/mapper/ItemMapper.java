package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Item;
import com.bookstory.store.web.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, OrderMapper.class})
public interface ItemMapper {

    @Mapping(target = "order", ignore = true)
    ItemDTO toDto(Item item);

    @Mapping(target = "order", ignore = true)
    Item toEntity(ItemDTO itemDTO);

    @Mapping(target = "order", ignore = true)
    List<ItemDTO> toDtoList(List<Item> items);
}
