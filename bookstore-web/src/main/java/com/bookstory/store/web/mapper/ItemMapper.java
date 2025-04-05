package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Item;
import com.bookstory.store.web.dto.ItemDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, OrderMapper.class})
public interface ItemMapper {

    ItemDTO toDto(Item item);

    Item toEntity(ItemDTO itemDTO);

    List<ItemDTO> toDtoList(List<Item> items);
}
