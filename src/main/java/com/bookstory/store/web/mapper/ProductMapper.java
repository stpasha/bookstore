package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);

    List<ProductDTO> toDtoList(List<Product> products);
}
