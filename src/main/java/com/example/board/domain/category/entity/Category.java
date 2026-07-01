package com.example.board.domain.category.entity;

import com.example.board.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_name",
                        columnNames = "name"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 50
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private CategoryStatus status;

    private Category(
            String name,
            CategoryStatus status
    ) {
        this.name = name;
        this.status = status;
    }

    public static Category create(String name) {
        return new Category(
                name,
                CategoryStatus.ACTIVE
        );
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void activate() {
        this.status = CategoryStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CategoryStatus.INACTIVE;
    }

    public boolean isActive() {
        return status == CategoryStatus.ACTIVE;
    }
}