package org.example.eatopia.domain.productImage.repository;

import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // 단건
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    // 여러 상품의 이미지 일괄 조회 (N+1 방지)
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds ORDER BY pi.displayOrder ASC")
    List<ProductImage> findAllByProductIdOrderByDisplayOrderAsc(@Param("productIds") List<Long> productIds);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isThumbnail = true")
    Optional<ProductImage> findThumbnailByProductId(@Param("productId") Long productId);

    long countByProductId(Long productId);
}
