package com.teamchallenge.easybuy.repository.goods.category;

import com.teamchallenge.easybuy.domain.model.goods.category.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, UUID>, JpaSpecificationExecutor<CategoryAttribute> {
}