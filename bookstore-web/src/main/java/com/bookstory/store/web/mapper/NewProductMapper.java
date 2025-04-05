package com.bookstory.store.web.mapper;

import com.bookstory.store.model.Product;
import com.bookstory.store.web.dto.NewProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewProductMapper {

    @Mapping(source = "imageName", target = "imageUrl")
    @Mapping(target = "id", ignore = true)
    Product toEntity(NewProductDTO dto);

    @Mapping(source = "imageUrl", target = "imageName")
    @Mapping(target = "baseImage", ignore = true)
    NewProductDTO toDto(Product entity);
}
