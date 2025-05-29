package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.CategoryDTO;
import com.teamchallenge.easybuy.exceptions.CategoryNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.CategoryMapper;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.repo.goods.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private UUID categoryId;
    private UUID parentId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        parentId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        category.setParentCategory(null);
        category.setSubcategories(new ArrayList<>());
        category.setAttributes(new ArrayList<>());

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setName("Electronics");
        categoryDTO.setParentId(null);
        categoryDTO.setSubcategoryIds(new ArrayList<>());
        categoryDTO.setAttributes(new ArrayList<>());
    }

    @Test
    @DisplayName("getAllCategories should return list of categories")
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange
        List<Category> categories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertEquals(expectedDTOs, result);
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getAllCategories should return empty list when no categories exist")
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getCategoryById should return category when found")
    void getCategoryById_ShouldReturnCategory_WhenFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.getCategoryById(categoryId);

        // Assert
        assertEquals(categoryDTO, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getCategoryById should throw CategoryNotFoundException when not found")
    void getCategoryById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createCategory should create and return new category")
    void createCategory_ShouldCreateAndReturnCategory() {
        // Arrange
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.createCategory(categoryDTO);

        // Assert
        assertEquals(categoryDTO, result);
        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("updateCategory should update and return updated category")
    void updateCategory_ShouldUpdateAndReturnCategory() {
        // Arrange
        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(categoryId);
        updatedDTO.setName("Updated Electronics");

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Updated Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toEntity(updatedDTO)).thenReturn(updatedCategory);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(updatedDTO);

        // Act
        CategoryDTO result = categoryService.updateCategory(categoryId, updatedDTO);

        // Assert
        assertEquals(updatedDTO, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toEntity(updatedDTO);
        verify(categoryRepository, times(1)).save(updatedCategory);
        verify(categoryMapper, times(1)).toDto(updatedCategory);
    }

    @Test
    @DisplayName("updateCategory should throw CategoryNotFoundException when not found")
    void updateCategory_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("deleteCategory should delete category when found")
    void deleteCategory_ShouldDeleteCategory_WhenFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(categoryId);

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("deleteCategory should throw CategoryNotFoundException when not found")
    void deleteCategory_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getAllRootCategories should return root categories")
    void getAllRootCategories_ShouldReturnRootCategories() {
        // Arrange
        List<Category> rootCategories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        when(categoryRepository.findAllRootCategories()).thenReturn(rootCategories);
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act
        List<CategoryDTO> result = categoryService.getAllRootCategories();

        // Assert
        assertEquals(expectedDTOs, result);
        verify(categoryRepository, times(1)).findAllRootCategories();
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getAllSubcategoriesByParentId should return subcategories")
    void getAllSubcategoriesByParentId_ShouldReturnSubcategories() {
        // Arrange
        Category subcategory = new Category();
        subcategory.setId(UUID.randomUUID());
        subcategory.setName("Laptops");
        subcategory.setParentCategory(category);

        CategoryDTO subcategoryDTO = new CategoryDTO();
        subcategoryDTO.setId(subcategory.getId());
        subcategoryDTO.setName("Laptops");
        subcategoryDTO.setParentId(categoryId);

        List<Category> subcategories = Collections.singletonList(subcategory);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(subcategoryDTO);

        when(categoryRepository.findAllSubcategoriesByParentId(categoryId)).thenReturn(subcategories);
        when(categoryMapper.toDto(subcategory)).thenReturn(subcategoryDTO);

        // Act
        List<CategoryDTO> result = categoryService.getAllSubcategoriesByParentId(categoryId);

        // Assert
        assertEquals(expectedDTOs, result);
        verify(categoryRepository, times(1)).findAllSubcategoriesByParentId(categoryId);
        verify(categoryMapper, times(1)).toDto(subcategory);
    }
}