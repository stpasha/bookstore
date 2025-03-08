package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.NewProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewProductMapper {

    @Mapping(source = "imageName", target = "imageUrl")
    Product toEntity(NewProductDTO dto);

    @Mapping(source = "imageUrl", target = "imageName")
    NewProductDTO toDto(Product entity);
}
