package com.bookflow.category;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAll(Sort sort);

    boolean existsByCategoryNameIgnoreCase(String name);

    Optional<Category> findByCategoryNameIgnoreCase(String name);
}
