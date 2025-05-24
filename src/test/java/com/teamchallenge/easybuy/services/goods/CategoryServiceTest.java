package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.CategoryDTO;
import com.teamchallenge.easybuy.exceptions.CategoryNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.CategoryMapper;
import com.teamchallenge.easybuy.models.goods.Category;
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
        // Подготовка тестовых данных
        categoryId = UUID.randomUUID();
        parentId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name("Mice")
                .description("Computer mice")
                .enabled(true)
                .parent(null)
                .subcategories(new HashSet<>())
                .build();

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setName("Mice");
        categoryDTO.setDescription("Computer mice");
        categoryDTO.setEnabled(true);
        categoryDTO.setParentId(null);
        categoryDTO.setSubcategoryIds(new HashSet<>());
        categoryDTO.setLevel(0);
        categoryDTO.setPath("Mice");
        categoryDTO.setHasSubcategories(false);
    }

    @Test
    @DisplayName("getAllCategories should return list of categories")
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange
        List<Category> categories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDTO);

        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertEquals(expectedDTOs, result);
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(category);
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
    @DisplayName("createCategory should set parent when parentId is provided")
    void createCategory_ShouldSetParent_WhenParentIdProvided() {
        // Arrange
        UUID parentId = UUID.randomUUID();
        Category parent = Category.builder().id(parentId).name("Electronics").build();
        categoryDTO.setParentId(parentId);

        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.createCategory(categoryDTO);

        // Assert
        assertEquals(categoryDTO, result);
        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        assertEquals(parent, category.getParent()); // Проверяем, что parent установлен
    }

    @Test
    @DisplayName("createCategory should throw CategoryNotFoundException when parentId is invalid")
    void createCategory_ShouldThrowException_WhenParentIdInvalid() {
        // Arrange
        categoryDTO.setParentId(parentId);
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.createCategory(categoryDTO));
        verify(categoryRepository, times(1)).findById(parentId);
        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCategory should update and return updated category")
    void updateCategory_ShouldUpdateAndReturnCategory() {
        // Arrange
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .name("Updated Mice")
                .description("Updated computer mice")
                .enabled(false)
                .parent(null)
                .subcategories(new HashSet<>())
                .build();

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(categoryId);
        updatedDTO.setName("Updated Mice");
        updatedDTO.setDescription("Updated computer mice");
        updatedDTO.setEnabled(false);
        updatedDTO.setParentId(null);
        updatedDTO.setSubcategoryIds(new HashSet<>());
        updatedDTO.setLevel(0);
        updatedDTO.setPath("Updated Mice");
        updatedDTO.setHasSubcategories(false);

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
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteCategory should delete category when found")
    void deleteCategory_ShouldDeleteCategory_WhenFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("deleteCategory should throw CategoryNotFoundException when not found")
    void deleteCategory_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getAllCategoryIds should return category IDs when found")
    void getAllCategoryIds_ShouldReturnCategoryIds_WhenFound() {
        // Arrange
        Category subcategory = Category.builder().id(UUID.randomUUID()).name("Wireless Mice").build();
        category.getSubcategories().add(subcategory);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Set<UUID> result = categoryService.getAllCategoryIds(categoryId);

        // Assert
        assertEquals(2, result.size()); // categoryId + subcategoryId
        assertTrue(result.contains(categoryId));
        assertTrue(result.contains(subcategory.getId()));
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("getAllCategoryIds should throw CategoryNotFoundException when not found")
    void getAllCategoryIds_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getAllCategoryIds(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
    }
}