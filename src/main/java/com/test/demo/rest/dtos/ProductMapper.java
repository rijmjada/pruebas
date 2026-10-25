package com.test.demo.rest.dtos;


import com.test.demo.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest response);

    ProductResponse toResponse(Product product);
}
