package com.bookflow.book;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "id", target = "book_id")
    BookDto toDto(Book book);

    @Mapping(source = "book_id", target = "id")
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toEntity(BookDto dto);
}
