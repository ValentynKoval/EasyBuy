package com.teamchallenge.easybuy.repo.goods.category;

import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, UUID>, JpaSpecificationExecutor<CategoryAttribute> {
}