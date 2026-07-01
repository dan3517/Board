package com.example.board.domain.category.repository;

import com.example.board.domain.category.entity.Category;
import com.example.board.domain.category.entity.CategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndStatus(
            Long id,
            CategoryStatus status
    );

    boolean existsByName(String name);
}