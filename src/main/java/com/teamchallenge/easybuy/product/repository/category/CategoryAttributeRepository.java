package com.teamchallenge.easybuy.product.repository.category;

import com.teamchallenge.easybuy.product.entity.category.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, UUID>, JpaSpecificationExecutor<CategoryAttribute> {
}