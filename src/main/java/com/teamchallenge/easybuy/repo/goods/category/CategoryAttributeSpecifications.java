package com.teamchallenge.easybuy.repo.goods.category;

import com.teamchallenge.easybuy.models.goods.category.AttributeType;
import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CategoryAttributeSpecifications {

    public static Specification<CategoryAttribute> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<CategoryAttribute> hasCategoryId(UUID categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<CategoryAttribute> hasType(AttributeType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }
}