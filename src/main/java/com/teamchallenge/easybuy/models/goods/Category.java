package com.teamchallenge.easybuy.models.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a category in the product catalog.
 * Categories can be nested to form a hierarchy of categories and subcategories.
 */

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "subcategories")
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_enabled", columnList = "enabled"),
        @Index(name = "idx_category_name", columnList = "name")
})
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier of the category", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @NonNull
    @Column(name = "name", nullable = false, length = 255)
    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    @NonNull
    @Schema(description = "Detailed description of the category",
            example = "All kinds of electronic devices and accessories")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Schema(description = "Parent category, if this is a subcategory")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Subcategories belonging to this category")
    private Set<Category> subcategories = new HashSet<>();

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    @Schema(description = "Whether the category is active and visible", example = "true")
    private boolean enabled = true;
}
