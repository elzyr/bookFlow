package com.bookflow.book;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "id", target = "book_id")
    BookDto toDto(Book book);

    @Mapping(source = "book_id", target = "id")
    Book toEntity(BookDto dto);
}
