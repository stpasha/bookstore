package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderMapper.class, ItemMapper.class})
public interface ProductMapper {

    @Mapping(target = "quantity", ignore = true)
    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);
}
