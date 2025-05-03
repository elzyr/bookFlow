package com.bookflow.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/categories/{id}")
                .buildAndExpand(created.getCategoryId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> all = categoryService.findAll();
        return ResponseEntity.ok(all);
    }
}
