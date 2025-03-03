package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Order;
import com.bookstory.store.web.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "items", ignore = true)
    OrderDTO toDto(Order order);

    Order toEntity(OrderDTO orderDTO);

    @Mapping(target = "items", ignore = true)
    List<OrderDTO> toDtoList(List<Order> orders);
}

