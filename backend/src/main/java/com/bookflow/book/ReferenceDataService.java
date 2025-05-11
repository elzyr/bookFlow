package com.bookflow.book;

import com.bookflow.author.Author;
import com.bookflow.author.AuthorRepository;
import com.bookflow.category.Category;
import com.bookflow.category.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceDataService {
    private final AuthorRepository authorRepo;
    private final CategoryRepository categoryRepo;

    @Transactional
    public List<Author> resolveAuthors(List<String> authorNames) {
        return authorNames.stream()
                .map(name -> authorRepo.findByName(name)
                        .orElseGet(() -> authorRepo.save(Author.builder().name(name).build()))
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Category> resolveCategories(List<String> categoryNames) {
        return categoryNames.stream()
                .map(cat -> categoryRepo.findByCategoryName(cat)
                        .orElseGet(() -> categoryRepo.save(Category.builder().categoryName(cat).build()))
                )
                .collect(Collectors.toList());
    }
}