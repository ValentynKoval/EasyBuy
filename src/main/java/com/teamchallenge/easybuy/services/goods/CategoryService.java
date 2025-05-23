package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.CategoryDTO;
import com.teamchallenge.easybuy.exceptions.CategoryNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.CategoryMapper;
import com.teamchallenge.easybuy.models.goods.Category;
import com.teamchallenge.easybuy.repo.goods.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing categories, including hierarchical operations and caching.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieves all categories with computed path, level, and hasSubcategories.
     * This method is cached to improve performance for frequent calls.
     *
     * @return List of CategoryDTO objects representing all categories.
     */
    @Cacheable(value = "categories", key = "'allCategories'")
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> toDtoWithHierarchy(category, new HashSet<>()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a category by its ID with computed path, level, and hasSubcategories.
     * This method is cached to avoid redundant hierarchy calculations.
     *
     * @param id The UUID of the category to retrieve.
     * @return CategoryDTO object for the specified category.
     * @throws CategoryNotFoundException if the category is not found.
     */
    @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toDtoWithHierarchy(category, new HashSet<>());
    }

    /**
     * Recursively converts a Category entity to CategoryDTO with computed path, level, and hasSubcategories.
     * Prevents infinite loops by tracking visited categories.
     *
     * @param category The Category entity to convert.
     * @param visited  Set of visited category IDs to avoid cycles.
     * @return CategoryDTO object or null if a cycle is detected.
     */
    private CategoryDTO toDtoWithHierarchy(Category category, Set<UUID> visited) {
        if (visited.contains(category.getId())) {
            return null; // Avoids infinite loops due to cyclic references
        }
        visited.add(category.getId());

        CategoryDTO dto = categoryMapper.toDto(category);
        // Computes level
        int level = calculateLevel(category);
        dto.setLevel(level);

        // Computes path
        String path = buildPath(category);
        dto.setPath(path);

        // Sets hasSubcategories
        boolean hasSubcategories = category.getSubcategories() != null && !category.getSubcategories().isEmpty();
        dto.setHasSubcategories(hasSubcategories);

        // Recursively processes parent and subcategories
        if (category.getParent() != null) {
            dto.setParent(toDtoWithHierarchy(category.getParent(), visited));
        }
        if (hasSubcategories) {
            Set<CategoryDTO> subcategories = category.getSubcategories().stream()
                    .map(sub -> toDtoWithHierarchy(sub, new HashSet<>(visited)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            dto.setSubcategories(subcategories);
        }

        return dto;
    }

    /**
     * Recursively calculates the level of a category in the hierarchy.
     * Root category (no parent) has level 0.
     *
     * @param category The category to calculate level for.
     * @return Integer representing the depth level.
     */
    private int calculateLevel(Category category) {
        if (category.getParent() == null) {
            return 0;
        }
        return calculateLevel(category.getParent()) + 1;
    }

    /**
     * Builds the hierarchical path from the root category to the current category.
     *
     * @param category The category to build the path for.
     * @return String representing the full path (e.g., "Electronics > Mice > Wireless Mice").
     */
    private String buildPath(Category category) {
        List<String> pathParts = new ArrayList<>();
        Category current = category;
        while (current != null) {
            pathParts.add(current.getName());
            current = current.getParent();
        }
        Collections.reverse(pathParts);
        return String.join(" > ", pathParts);
    }

    /**
     * Retrieves all IDs of a category and its subcategories for filtering goods.
     * This method is cached to optimize recursive operations.
     *
     * @param categoryId The UUID of the starting category.
     * @return Set of UUIDs including the category and all its subcategories.
     * @throws CategoryNotFoundException if the category is not found.
     */
    @Cacheable(value = "categoryIds", key = "#categoryId")
    public Set<UUID> getAllCategoryIds(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        Set<UUID> categoryIds = new HashSet<>();
        collectCategoryIds(category, categoryIds);
        return categoryIds;
    }

    /**
     * Recursively collects all IDs of a category and its subcategories.
     *
     * @param category The category to start collecting from.
     * @param categoryIds The set to add IDs to.
     */
    private void collectCategoryIds(Category category, Set<UUID> categoryIds) {
        categoryIds.add(category.getId());
        if (category.getSubcategories() != null) {
            for (Category subcategory : category.getSubcategories()) {
                collectCategoryIds(subcategory, categoryIds);
            }
        }
    }
}