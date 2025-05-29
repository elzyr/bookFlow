package com.bookflow.category;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category entity);

    List<CategoryDto> toDtoList(List<Category> entities);
}
