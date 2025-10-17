package org.example.eatopia.domain.category.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int depth;

    // 자기 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Builder(access = AccessLevel.PRIVATE)
    private Category(String name, Integer depth, Category parent) {
        this.name = name;
        this.depth = depth;
        this.parent = parent;
    }

    // 생성
    public static Category create(String name, Category parent) {
        int depth = (parent == null) ? 1 : 2;
        return Category.builder()
                .name(name)
                .depth(depth)
                .parent(parent)
                .build();
    }
}
