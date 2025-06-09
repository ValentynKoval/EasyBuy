package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.exceptions.goods.GoodsNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.GoodsMapper;
import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.repo.goods.GoodsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {

    @Mock
    private GoodsRepository goodsRepository; // Mock the repository dependency

    @Mock
    private GoodsMapper goodsMapper; // Mock the mapper dependency

    @InjectMocks
    private GoodsService goodsService; // Inject mocks into the service under test

    private Goods goods;
    private GoodsDTO goodsDTO;
    private UUID goodsId;
    private UUID categoryId;
    private Category category;

    @BeforeEach
    void setUp() {
        // Initialize common test data before each test method
        goodsId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        goods = new Goods();
        goods.setId(goodsId);
        goods.setArt("ART-001");
        goods.setName("Wireless Mouse");
        goods.setDescription("A high-precision wireless mouse.");
        goods.setPrice(new BigDecimal("1499.99"));
        goods.setMainImageUrl("https://example.com/images/mouse.jpg");
        goods.setStock(120);
        goods.setReviewsCount(45);
        goods.setShopId(UUID.randomUUID());
        goods.setCategory(category);
        goods.setGoodsStatus(Goods.GoodsStatus.ACTIVE); // Using Enum type
        goods.setDiscountStatus(Goods.DiscountStatus.NONE); // Using Enum type
        goods.setDiscountValue(null);
        goods.setRating(4);
        goods.setSlug("wireless-mouse-123");
        goods.setMetaTitle("Wireless Mouse - Best Price");
        goods.setMetaDescription("High-quality wireless mouse at the best price.");
        goods.setCreatedAt(Instant.now());
        goods.setUpdatedAt(Instant.now());
        goods.setAdditionalImages(new ArrayList<>()); // Assuming Goods has this field

        goodsDTO = new GoodsDTO();
        goodsDTO.setId(goodsId);
        goodsDTO.setArt("ART-001");
        goodsDTO.setName("Wireless Mouse");
        goodsDTO.setDescription("A high-precision wireless mouse.");
        goodsDTO.setPrice(new BigDecimal("1499.99"));
        goodsDTO.setMainImageUrl("https://example.com/images/mouse.jpg");
        goodsDTO.setStock(120);
        goodsDTO.setReviewsCount(45);
        goodsDTO.setShopId(goods.getShopId());
        goodsDTO.setCategoryId(categoryId);
        goodsDTO.setGoodsStatus(Goods.GoodsStatus.ACTIVE); // Changed to Enum type
        goodsDTO.setDiscountStatus(Goods.DiscountStatus.NONE); // Changed to Enum type
        goodsDTO.setDiscountValue(null);
        goodsDTO.setRating(4);
        goodsDTO.setSlug("wireless-mouse-123");
        goodsDTO.setMetaTitle("Wireless Mouse - Best Price");
        goodsDTO.setMetaDescription("High-quality wireless mouse at the best price.");
        goodsDTO.setCreatedAt(goods.getCreatedAt());
        goodsDTO.setUpdatedAt(goods.getUpdatedAt());
        // If GoodsDTO.additionalImages is List<GoodsImageDTO>, you might need to mock or provide a simple GoodsImageDTO list.
        // For now, assuming GoodsDTO.additionalImages is handled by mapper correctly for basic cases.
        // If GoodsDTO has additionalImageUrls, it should be List<String> or List<GoodsImageDTO>. Assuming it's `additionalImages` (List<GoodsImageDTO>) based on Swagger.
        goodsDTO.setAdditionalImages(new ArrayList<>());
    }

    @Test
    @DisplayName("getAllGoods should return a list of goods DTOs")
    void getAllGoods_ShouldReturnListOfGoods() {
        // Arrange: Prepare the mock behavior
        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        // When goodsRepository.findAll() is called, return our mock goodsList
        when(goodsRepository.findAll()).thenReturn(goodsList);
        // When goodsMapper.toDto(goods) is called for the specific goods, return our mock goodsDTO
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act: Call the method under test
        List<GoodsDTO> result = goodsService.getAllGoods();

        // Assert: Verify the results and mock interactions
        // Ensure the returned list matches our expected DTOs
        assertEquals(expectedDTOs, result);
        // Verify that findAll() was called exactly once on the repository
        verify(goodsRepository, times(1)).findAll();
        // Verify that toDto() was called exactly once on the mapper with the specific goods object
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getAllGoods should return an empty list when no goods exist")
    void getAllGoods_ShouldReturnEmptyList_WhenNoGoodsExist() {
        // Arrange: Mock the repository to return an empty list
        when(goodsRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Call the method under test
        List<GoodsDTO> result = goodsService.getAllGoods();

        // Assert: Verify the result and mock interactions
        // Ensure the returned list is empty
        assertTrue(result.isEmpty());
        // Verify that findAll() was called exactly once
        verify(goodsRepository, times(1)).findAll();
        // Verify that toDto() was never called on the mapper, as there are no goods to map
        verify(goodsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getGoodsById should return goods DTO when found")
    void getGoodsById_ShouldReturnGoods_WhenFound() {
        // Arrange: Mock the repository to return an Optional containing the goods
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        // Mock the mapper to convert the goods to goodsDTO
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act: Call the method under test
        GoodsDTO result = goodsService.getGoodsById(goodsId);

        // Assert: Verify the result and mock interactions
        // Ensure the returned DTO matches our expected goodsDTO
        assertEquals(goodsDTO, result);
        // Verify findById() was called once with the correct ID
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify toDto() was called once with the goods object
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getGoodsById should throw GoodsNotFoundException when not found")
    void getGoodsById_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock the repository to return an empty Optional
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(GoodsNotFoundException.class, () -> goodsService.getGoodsById(goodsId));
        // Verify findById() was called once
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify toDto() was never called, as no goods were found
        verify(goodsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createGoods should create and return a new goods DTO")
    void createGoods_ShouldCreateAndReturnGoods() {
        // Arrange: Mock all necessary interactions for creation
        // Mock that no goods with the same ART already exist
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(false);
        // Mock the mapper to convert DTO to entity
        when(goodsMapper.toEntity(goodsDTO)).thenReturn(goods);
        // Mock the repository save operation to return the saved entity
        when(goodsRepository.save(goods)).thenReturn(goods);
        // Mock the mapper to convert the saved entity back to DTO
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act: Call the method under test
        GoodsDTO result = goodsService.createGoods(goodsDTO);

        // Assert: Verify the result and mock interactions
        // Ensure the returned DTO matches our expected goodsDTO
        assertEquals(goodsDTO, result);
        // Verify existsByArt() was called once
        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
        // Verify toEntity() was called once
        verify(goodsMapper, times(1)).toEntity(goodsDTO);
        // Verify save() was called once
        verify(goodsRepository, times(1)).save(goods);
        // Verify toDto() was called once
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("createGoods should throw IllegalArgumentException when goods ART already exists")
    void createGoods_ShouldThrowException_WhenArtExists() {
        // Arrange: Mock that goods with the same ART already exist
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(true);

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(IllegalArgumentException.class, () -> goodsService.createGoods(goodsDTO));
        // Verify existsByArt() was called once
        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
        // Verify that toEntity() and save() were never called
        verify(goodsMapper, never()).toEntity(any());
        verify(goodsRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateGoods should update and return updated goods DTO")
    void updateGoods_ShouldUpdateAndReturnGoods() {
        // Arrange: Prepare data for update and mock interactions
        GoodsDTO updatedDTO = new GoodsDTO();
        updatedDTO.setId(goodsId);
        updatedDTO.setArt("ART-002");
        updatedDTO.setName("Updated Mouse");
        updatedDTO.setPrice(new BigDecimal("1999.99"));
        updatedDTO.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        updatedDTO.setDiscountStatus(Goods.DiscountStatus.ACTIVE);

        Goods updatedGoodsEntity = new Goods();
        updatedGoodsEntity.setId(goodsId);
        updatedGoodsEntity.setArt("ART-002");
        updatedGoodsEntity.setName("Updated Mouse");
        updatedGoodsEntity.setPrice(new BigDecimal("1999.99"));
        updatedGoodsEntity.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        updatedGoodsEntity.setDiscountStatus(Goods.DiscountStatus.ACTIVE);
        // Ensure other fields from existingGoods are set if they are not null or primitive
        updatedGoodsEntity.setShopId(goods.getShopId());
        updatedGoodsEntity.setCategory(goods.getCategory());
        updatedGoodsEntity.setReviewsCount(goods.getReviewsCount());
        updatedGoodsEntity.setStock(goods.getStock());
        updatedGoodsEntity.setMainImageUrl(goods.getMainImageUrl());
        updatedGoodsEntity.setDescription(goods.getDescription());
        updatedGoodsEntity.setSlug(goods.getSlug());
        updatedGoodsEntity.setMetaTitle(goods.getMetaTitle());
        updatedGoodsEntity.setMetaDescription(goods.getMetaDescription());
        updatedGoodsEntity.setCreatedAt(goods.getCreatedAt());


        // Mock finding the existing goods by ID
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods)); // `goods` is the original entity
        // Mock that no other goods with the new ART already exist
        when(goodsRepository.existsByArt("ART-002")).thenReturn(false);
        // Mock the mapper to convert the updated DTO to an entity
        when(goodsMapper.toEntity(updatedDTO)).thenReturn(updatedGoodsEntity);
        // Mock saving the updated entity and returning it
        when(goodsRepository.save(updatedGoodsEntity)).thenReturn(updatedGoodsEntity);
        // Mock the mapper to convert the updated entity back to DTO
        when(goodsMapper.toDto(updatedGoodsEntity)).thenReturn(updatedDTO);

        // Act: Call the method under test
        GoodsDTO result = goodsService.updateGoods(goodsId, updatedDTO);

        // Assert: Verify the result and mock interactions
        // Ensure the returned DTO matches our expected updatedDTO
        assertEquals(updatedDTO, result);
        // Verify findById() was called once to get the existing goods
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify existsByArt() was called once with the new ART
        verify(goodsRepository, times(1)).existsByArt("ART-002");
        // Verify toEntity() was called once with the updated DTO
        verify(goodsMapper, times(1)).toEntity(updatedDTO);
        // Verify save() was called once with the updated entity
        verify(goodsRepository, times(1)).save(updatedGoodsEntity);
        // Verify toDto() was called once with the updated entity
        verify(goodsMapper, times(1)).toDto(updatedGoodsEntity);
    }

    @Test
    @DisplayName("updateGoods should throw GoodsNotFoundException when goods to update are not found")
    void updateGoods_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock that no goods are found for the given ID
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(GoodsNotFoundException.class, () -> goodsService.updateGoods(goodsId, goodsDTO));
        // Verify findById() was called once
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify that existsByArt() and toEntity() were never called
        verify(goodsRepository, never()).existsByArt(any());
        verify(goodsMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("updateGoods should throw IllegalArgumentException when new ART already exists for another goods")
    void updateGoods_ShouldThrowException_WhenNewArtExistsForOtherGoods() {
        // Arrange
        GoodsDTO updatedDTO = new GoodsDTO();
        updatedDTO.setId(UUID.randomUUID()); // Different ID to simulate update of *another* goods
        updatedDTO.setArt("ART-001"); // Attempt to update with an existing ART (original goods has ART-001)
        updatedDTO.setName("Another Mouse");
        updatedDTO.setPrice(new BigDecimal("100.00"));
        updatedDTO.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        updatedDTO.setDiscountStatus(Goods.DiscountStatus.NONE);

        // Mock that the original goods (goodsId) is found
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        // Mock that the new ART ("ART-001") already exists in the repository (and it's not the original goods' ART)
        when(goodsRepository.existsByArt("ART-001")).thenReturn(true);


        // Act & Assert
        // We're updating `goodsId` but trying to set its ART to "ART-001", which `goods` already has.
        // The condition for throwing `IllegalArgumentException` is:
        // `!existingGoods.getArt().equals(goodsDTO.getArt()) && goodsRepository.existsByArt(goodsDTO.getArt())`
        // In this specific test, `existingGoods.getArt()` is "ART-001", `goodsDTO.getArt()` is "ART-001".
        // So `!existingGoods.getArt().equals(goodsDTO.getArt())` will be `false`.
        // This test case would *not* throw an IllegalArgumentException based on the current logic in `GoodsService`.
        // Let's adjust this test to correctly simulate an attempt to use an ART that belongs to *another* item.
        // We need `existingGoods.getArt()` to be different from `goodsDTO.getArt()` and for `goodsDTO.getArt()` to exist.

        // Let's modify `goods` to have a different ART initially for this specific test case.
        Goods existingGoodsWithDifferentArt = new Goods();
        existingGoodsWithDifferentArt.setId(goodsId);
        existingGoodsWithDifferentArt.setArt("ORIGINAL-ART"); // This is the ART of the goods being updated
        existingGoodsWithDifferentArt.setName("Original Mouse");
        existingGoodsWithDifferentArt.setPrice(new BigDecimal("500.00"));
        existingGoodsWithDifferentArt.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        existingGoodsWithDifferentArt.setDiscountStatus(Goods.DiscountStatus.NONE);
        existingGoodsWithDifferentArt.setShopId(UUID.randomUUID());
        existingGoodsWithDifferentArt.setCategory(category);


        // Mock that existing goods with ART "ORIGINAL-ART" is found
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(existingGoodsWithDifferentArt));
        // Mock that a *different* ART ("ART-001") already exists for *another* goods
        when(goodsRepository.existsByArt("ART-001")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> goodsService.updateGoods(goodsId, goodsDTO)); // goodsDTO still has ART-001
        // Verify findById() was called once
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify existsByArt() was called once with "ART-001"
        verify(goodsRepository, times(1)).existsByArt("ART-001");
        // Verify that toEntity() and save() were never called
        verify(goodsMapper, never()).toEntity(any());
        verify(goodsRepository, never()).save(any());
    }


    @Test
    @DisplayName("deleteGoods should delete goods when found")
    void deleteGoods_ShouldDeleteGoods_WhenFound() {
        // Arrange: Mock finding the goods by ID
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        // Mock the delete operation (it returns void, so use doNothing().when())
        doNothing().when(goodsRepository).deleteById(goodsId);

        // Act: Call the method under test
        goodsService.deleteGoods(goodsId);

        // Assert: Verify mock interactions
        // Verify findById() was called once
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify deleteById() was called once
        verify(goodsRepository, times(1)).deleteById(goodsId);
    }

    @Test
    @DisplayName("deleteGoods should throw GoodsNotFoundException when goods to delete are not found")
    void deleteGoods_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock that no goods are found for the given ID
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling the method throws the expected exception
        assertThrows(GoodsNotFoundException.class, () -> goodsService.deleteGoods(goodsId));
        // Verify findById() was called once
        verify(goodsRepository, times(1)).findById(goodsId);
        // Verify deleteById() was never called
        verify(goodsRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("searchGoods should return matching goods DTOs based on criteria")
    void searchGoods_ShouldReturnMatchingGoods() {
        // Arrange: Prepare a list of goods and their DTOs for the search result
        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        // Mock the repository to return the goodsList when findAll is called with any Specification
        when(goodsRepository.findAll(any(Specification.class))).thenReturn(goodsList);
        // Mock the mapper to convert the goods entity to DTO
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act: Call the searchGoods method with some criteria (e.g., by ART)
        List<GoodsDTO> result = goodsService.searchGoods(null, "ART-001", null, null, null, null, null, null, null, null, null);

        // Assert: Verify the result and mock interactions
        // Ensure the returned list matches our expected DTOs
        assertEquals(expectedDTOs, result);
        // Verify findAll() was called once with any Specification object
        verify(goodsRepository, times(1)).findAll(any(Specification.class));
        // Verify toDto() was called once with the goods entity
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("searchGoods should return an empty list when no matches are found")
    void searchGoods_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange: Mock the repository to return an empty list for any Specification
        when(goodsRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        // Act: Call the searchGoods method with criteria that should yield no results
        List<GoodsDTO> result = goodsService.searchGoods(null, "NonExistentArt", null, null, null, null, null, null, null, null, null);

        // Assert: Verify the result and mock interactions
        // Ensure the returned list is empty
        assertTrue(result.isEmpty());
        // Verify findAll() was called once with any Specification object
        verify(goodsRepository, times(1)).findAll(any(Specification.class));
        // Verify toDto() was never called, as there were no goods to map
        verify(goodsMapper, never()).toDto(any());
    }
}