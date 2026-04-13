package com.teamchallenge.easybuy.product.repository.category;

import com.teamchallenge.easybuy.product.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    List<Category> findAllRootCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Category> findAllSubcategoriesByParentId(UUID parentId);
}