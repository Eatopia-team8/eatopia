package org.example.eatopia.domain.category.repository;

import org.example.eatopia.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
