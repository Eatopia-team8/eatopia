package org.example.eatopia.domain.product.repository;

import org.example.eatopia.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}