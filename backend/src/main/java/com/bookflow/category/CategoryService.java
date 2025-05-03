package com.bookflow.category;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        Category toSave = Category.builder()
                .categoryName(name)
                .build();

        return categoryRepository.save(toSave);
    }

    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category getOrCreate(CategoryInputDto dto) {
        if (dto.getCategoryId() != null) {
            return categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Kategoria nie istnieje: " + dto.getCategoryId()));
        }
        String name = dto.getCategoryName().trim();
        return categoryRepository.findByCategoryNameIgnoreCase(name)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .categoryName(name)
                        .build()));
    }

    public List<Category> getAllOrCreate(List<CategoryInputDto> dtos) {
        return dtos.stream()
                .map(this::getOrCreate)
                .toList();
    }
}
