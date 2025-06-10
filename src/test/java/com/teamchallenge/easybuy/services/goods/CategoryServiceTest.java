package com.teamchallenge.easybuy.services.goods;


import com.teamchallenge.easybuy.dto.goods.category.CategoryDTO;
import com.teamchallenge.easybuy.exceptions.goods.CategoryNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.category.CategoryMapper;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.repo.goods.category.CategoryRepository;
import com.teamchallenge.easybuy.services.goods.category.CategoryService;
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
    private CategoryRepository categoryRepository; // Mock the repository dependency

    @Mock
    private CategoryMapper categoryMapper; // Mock the mapper dependency

    @InjectMocks
    private CategoryService categoryService; // Inject mocks into the service under test

    private Category category;
    private CategoryDTO categoryDTO;
    private UUID categoryId;
    private UUID parentId; // Declared but not explicitly used in all tests, good to keep for potential future tests

    @BeforeEach
    void setUp() {
        // Initialize common test data before each test method
        categoryId = UUID.randomUUID();
        parentId = UUID.randomUUID(); // Example parent ID, not used for the root category setup

        // Setup a Category entity
        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        category.setParentCategory(null); // This is a root category
        category.setSubcategories(new ArrayList<>());
        category.setAttributes(new ArrayList<>());

        // Setup a CategoryDTO
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setName("Electronics");
        categoryDTO.setParentId(null); // This is a root category DTO
        categoryDTO.setSubcategoryIds(new ArrayList<>());
        categoryDTO.setAttributes(new ArrayList<>());
    }

    @Test
    @DisplayName("getAllCategories should return a list of category DTOs")
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange: Prepare the mock behavior
        List<Category> categories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        // When categoryRepository.findAll() is called, return our mock categories list
        when(categoryRepository.findAll()).thenReturn(categories);
        // When categoryMapper.toDto(category) is called, return our mock categoryDTO
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act: Call the method under test
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert: Verify the results and mock interactions
        // Ensure the returned list matches our expected DTOs
        assertEquals(expectedDTOs, result);
        // Verify that findAll() was called exactly once on the repository
        verify(categoryRepository, times(1)).findAll();
        // Verify that toDto() was called exactly once on the mapper with the specific category object
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getAllCategories should return an empty list when no categories exist")
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        // Arrange: Mock the repository to return an empty list
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Call the method under test
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert: Verify the result and mock interactions
        // Ensure the returned list is empty
        assertTrue(result.isEmpty());
        // Verify that findAll() was called exactly once
        verify(categoryRepository, times(1)).findAll();
        // Verify that toDto() was never called on the mapper, as there are no categories to map
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getCategoryById should return category DTO when found")
    void getCategoryById_ShouldReturnCategory_WhenFound() {
        // Arrange: Mock the repository to return an Optional containing the category
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        // Mock the mapper to convert the category to categoryDTO
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act: Call the method under test
        CategoryDTO result = categoryService.getCategoryById(categoryId);

        // Assert: Verify the result and mock interactions
        // Ensure the returned DTO matches our expected categoryDTO
        assertEquals(categoryDTO, result);
        // Verify findById() was called once with the correct ID
        verify(categoryRepository, times(1)).findById(categoryId);
        // Verify toDto() was called once with the category object
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getCategoryById should throw CategoryNotFoundException when not found")
    void getCategoryById_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock the repository to return an empty Optional
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        // Verify findById() was called once
        verify(categoryRepository, times(1)).findById(categoryId);
        // Verify toDto() was never called, as no category was found
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createCategory should create and return a new category DTO")
    void createCategory_ShouldCreateAndReturnCategory() {
        // Arrange: Mock all necessary interactions for creation
        // Mock the mapper to convert DTO to entity
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        // Mock the repository save operation to return the saved entity
        when(categoryRepository.save(category)).thenReturn(category);
        // Mock the mapper to convert the saved entity back to DTO
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act: Call the method under test
        CategoryDTO result = categoryService.createCategory(categoryDTO);

        // Assert: Verify the result and mock interactions
        // Ensure the returned DTO matches our expected categoryDTO
        assertEquals(categoryDTO, result);
        // Verify toEntity() was called once
        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        // Verify save() was called once
        verify(categoryRepository, times(1)).save(category);
        // Verify toDto() was called once
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("updateCategory should update and return the updated category DTO")
    void updateCategory_ShouldUpdateAndReturnCategory() {
        // Arrange: Prepare data for update and mock interactions
        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(categoryId);
        updatedDTO.setName("Updated Electronics");
        updatedDTO.setParentId(parentId); // Simulate updating parent
        updatedDTO.setSubcategoryIds(Arrays.asList(UUID.randomUUID())); // Simulate updating subcategories
        updatedDTO.setAttributes(new ArrayList<>()); // Simulate updating attributes

        Category updatedCategoryEntity = new Category();
        updatedCategoryEntity.setId(categoryId);
        updatedCategoryEntity.setName("Updated Electronics");
        updatedCategoryEntity.setParentCategory(new Category()); // Mock parent
        updatedCategoryEntity.setSubcategories(new ArrayList<>()); // Mock subcategories
        updatedCategoryEntity.setAttributes(new ArrayList<>()); // Mock attributes

        // Mock finding the existing category by ID
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category)); // `category` is the original entity

        // Mock the mapper to convert the updated DTO to an entity.
        // IMPORTANT: If your mapper simply creates a new entity, and doesn't update `existingCategory`,
        // ensure all necessary fields from `existingCategory` are also set in `updatedCategoryEntity`
        // if they are not part of `updatedDTO` but should be preserved.
        when(categoryMapper.toEntity(updatedDTO)).thenReturn(updatedCategoryEntity);

        // Mock saving the updated entity and returning it
        when(categoryRepository.save(updatedCategoryEntity)).thenReturn(updatedCategoryEntity);
        // Mock the mapper to convert the updated entity back to DTO
        when(categoryMapper.toDto(updatedCategoryEntity)).thenReturn(updatedDTO);

        // Act: Call the method under test
        CategoryDTO result = categoryService.updateCategory(categoryId, updatedDTO);

        // Assert: Verify the result and mock interactions
        // Ensure the returned DTO matches our expected updatedDTO
        assertEquals(updatedDTO, result);
        // Verify findById() was called once to get the existing category
        verify(categoryRepository, times(1)).findById(categoryId);
        // Verify toEntity() was called once with the updated DTO
        verify(categoryMapper, times(1)).toEntity(updatedDTO);
        // Verify save() was called once with the updated entity
        verify(categoryRepository, times(1)).save(updatedCategoryEntity);
        // Verify toDto() was called once with the updated entity
        verify(categoryMapper, times(1)).toDto(updatedCategoryEntity);
    }

    @Test
    @DisplayName("updateCategory should throw CategoryNotFoundException when category to update is not found")
    void updateCategory_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock that no category is found for the given ID
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));
        // Verify findById() was called once
        verify(categoryRepository, times(1)).findById(categoryId);
        // Verify that toEntity() was never called
        verify(categoryMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("deleteCategory should delete category when found")
    void deleteCategory_ShouldDeleteCategory_WhenFound() {
        // Arrange: Mock finding the category by ID
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        // Mock the delete operation (it returns void, so use doNothing().when())
        doNothing().when(categoryRepository).deleteById(categoryId);

        // Act: Call the method under test
        categoryService.deleteCategory(categoryId);

        // Assert: Verify mock interactions
        // Verify findById() was called once
        verify(categoryRepository, times(1)).findById(categoryId);
        // Verify deleteById() was called once
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("deleteCategory should throw CategoryNotFoundException when category to delete is not found")
    void deleteCategory_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock that no category is found for the given ID
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        // Verify findById() was called once
        verify(categoryRepository, times(1)).findById(categoryId);
        // Verify deleteById() was never called
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getAllRootCategories should return a list of root category DTOs")
    void getAllRootCategories_ShouldReturnRootCategories() {
        // Arrange: Prepare the mock behavior for root categories
        List<Category> rootCategories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        // When categoryRepository.findAllRootCategories() is called, return our mock root categories list
        when(categoryRepository.findAllRootCategories()).thenReturn(rootCategories);
        // When categoryMapper.toDto(category) is called, return our mock categoryDTO
        when(categoryMapper.toDto(category)).thenReturn(categoryDTO);

        // Act: Call the method under test
        List<CategoryDTO> result = categoryService.getAllRootCategories();

        // Assert: Verify the results and mock interactions
        // Ensure the returned list matches our expected DTOs
        assertEquals(expectedDTOs, result);
        // Verify that findAllRootCategories() was called exactly once on the repository
        verify(categoryRepository, times(1)).findAllRootCategories();
        // Verify that toDto() was called exactly once on the mapper with the specific category object
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getAllSubcategoriesByParentId should return a list of subcategory DTOs")
    void getAllSubcategoriesByParentId_ShouldReturnSubcategories() {
        // Arrange: Prepare data for subcategories and mock interactions
        Category subcategory = new Category();
        subcategory.setId(UUID.randomUUID());
        subcategory.setName("Laptops");
        subcategory.setParentCategory(category); // Set the parent category relationship

        CategoryDTO subcategoryDTO = new CategoryDTO();
        subcategoryDTO.setId(subcategory.getId());
        subcategoryDTO.setName("Laptops");
        subcategoryDTO.setParentId(categoryId); // The ID of the parent category

        List<Category> subcategories = Collections.singletonList(subcategory);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(subcategoryDTO);

        // When categoryRepository.findAllSubcategoriesByParentId() is called, return our mock subcategories list
        when(categoryRepository.findAllSubcategoriesByParentId(categoryId)).thenReturn(subcategories);
        // When categoryMapper.toDto(subcategory) is called, return our mock subcategoryDTO
        when(categoryMapper.toDto(subcategory)).thenReturn(subcategoryDTO);

        // Act: Call the method under test
        List<CategoryDTO> result = categoryService.getAllSubcategoriesByParentId(categoryId);

        // Assert: Verify the results and mock interactions
        // Ensure the returned list matches our expected DTOs
        assertEquals(expectedDTOs, result);
        // Verify that findAllSubcategoriesByParentId() was called exactly once on the repository
        verify(categoryRepository, times(1)).findAllSubcategoriesByParentId(categoryId);
        // Verify that toDto() was called exactly once on the mapper with the specific subcategory object
        verify(categoryMapper, times(1)).toDto(subcategory);
    }
}