package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.author.AuthorDto;
import com.bookflow.category.Category;
import com.bookflow.category.CategoryDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-13T20:33:26+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto toDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDto.BookDtoBuilder bookDto = BookDto.builder();

        bookDto.book_id( book.getId() );
        bookDto.title( book.getTitle() );
        bookDto.yearRelease( book.getYearRelease() );
        bookDto.language( book.getLanguage() );
        bookDto.jpg( book.getJpg() );
        bookDto.pageCount( book.getPageCount() );
        bookDto.description( book.getDescription() );
        bookDto.authors( authorListToAuthorDtoList( book.getAuthors() ) );
        bookDto.categories( categoryListToCategoryDtoList( book.getCategories() ) );
        bookDto.totalCopies( book.getTotalCopies() );
        bookDto.availableCopies( book.getAvailableCopies() );

        return bookDto.build();
    }

    @Override
    public Book toEntity(BookDto dto) {
        if ( dto == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.id( dto.getBook_id() );
        book.title( dto.getTitle() );
        book.yearRelease( dto.getYearRelease() );
        book.language( dto.getLanguage() );
        book.jpg( dto.getJpg() );
        book.pageCount( dto.getPageCount() );
        book.description( dto.getDescription() );
        book.totalCopies( dto.getTotalCopies() );
        book.availableCopies( dto.getAvailableCopies() );

        return book.build();
    }

    protected AuthorDto authorToAuthorDto(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorDto.AuthorDtoBuilder authorDto = AuthorDto.builder();

        authorDto.name( author.getName() );
        authorDto.information( author.getInformation() );

        return authorDto.build();
    }

    protected List<AuthorDto> authorListToAuthorDtoList(List<Author> list) {
        if ( list == null ) {
            return null;
        }

        List<AuthorDto> list1 = new ArrayList<AuthorDto>( list.size() );
        for ( Author author : list ) {
            list1.add( authorToAuthorDto( author ) );
        }

        return list1;
    }

    protected CategoryDto categoryToCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setCategoryId( category.getCategoryId() );
        categoryDto.setCategoryName( category.getCategoryName() );

        return categoryDto;
    }

    protected List<CategoryDto> categoryListToCategoryDtoList(List<Category> list) {
        if ( list == null ) {
            return null;
        }

        List<CategoryDto> list1 = new ArrayList<CategoryDto>( list.size() );
        for ( Category category : list ) {
            list1.add( categoryToCategoryDto( category ) );
        }

        return list1;
    }
}
