package org.example.eatopia.domain.category.repository;

import org.example.eatopia.domain.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    boolean existsByParentId(Long parentId);

    @Query("SELECT c FROM Category c ORDER BY c.id")
    List<Category> findAllByOrderById();

    // 상위 카테고리 페이징 조회
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    Page<Category> findParentCategories(Pageable pageable);

    // 부모 ID 목록으로 하위 카테고리 일괄 조회
    @Query("SELECT c FROM Category c WHERE c.parent.id IN :parentIds ORDER BY c.id")
    List<Category> findByParentIdIn(@Param("parentIds") List<Long> parentIds);
}
