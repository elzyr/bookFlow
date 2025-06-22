package com.bookflow.category;

import com.bookflow.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(Category incoming) {
        String name = incoming.getCategoryName().trim();

        if (categoryRepository.existsByCategoryNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Kategoria o takiej nazwie już istnieje");
        }

        Category toSave = Category.builder().categoryName(name).build();
        toSave.setBooks(new ArrayList<>());
        return categoryRepository.save(toSave);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Nie znaleziono kategorii"));
    }

    @Transactional
    public List<Category> getAllOrCreateCategories(List<String> categoryNames) {
        List<Category> result = new ArrayList<>();

        for (String rawName : categoryNames) {
            String name = rawName.trim();

            Optional<Category> existing = categoryRepository.findByCategoryNameIgnoreCase(name);
            Category category;
            if (existing.isPresent()) {
                category = existing.get();
            }
            else {
                category = createCategory(Category.builder().categoryName(name).build());
            }

            result.add(category);
        }

        return result;
    }
}
