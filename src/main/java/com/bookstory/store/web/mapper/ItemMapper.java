package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Item;
import com.bookstory.store.web.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDTO toDto(Item item);

    Item toEntity(ItemDTO itemDTO);

    List<ItemDTO> toDtoList(List<Item> items);
}
