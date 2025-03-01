package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Order;
import com.bookstory.store.web.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDto(Order order);

    Order toEntity(OrderDTO orderDTO);

    List<OrderDTO> toDtoList(List<Order> orders);
}
