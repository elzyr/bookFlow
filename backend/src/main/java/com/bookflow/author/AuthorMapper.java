package com.bookflow.author;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(source = "author_id", target = "authorId")
    AuthorDto toDto(Author entity);

    @Mapping(source = "author_id", target = "authorId")
    List<AuthorDto> toDtoList(List<Author> entities);
}