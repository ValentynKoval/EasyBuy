//package com.teamchallenge.easybuy.services.goods;
//
//import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
//import com.teamchallenge.easybuy.exceptions.goods.GoodsNotFoundException;
//import com.teamchallenge.easybuy.mapper.goods.GoodsMapper;
//import com.teamchallenge.easybuy.models.goods.Goods;
//import com.teamchallenge.easybuy.models.goods.category.Category;
//import com.teamchallenge.easybuy.repo.goods.GoodsRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class GoodsServiceTest {
//
//    @Mock
//    private GoodsRepository goodsRepository;
//
//    @Mock
//    private GoodsMapper goodsMapper;
//
//    @InjectMocks
//    private GoodsService goodsService;
//
//    private Goods goods;
//    private GoodsDTO goodsDTO;
//    private UUID goodsId;
//    private UUID categoryId;
//    private Category category;
//
//    @BeforeEach
//    void setUp() {
//        goodsId = UUID.randomUUID();
//        categoryId = UUID.randomUUID();
//
//        category = new Category();
//        category.setId(categoryId);
//        category.setName("Electronics");
//
//        goods = new Goods();
//        goods.setId(goodsId);
//        goods.setArt("ART-001");
//        goods.setName("Wireless Mouse");
//        goods.setDescription("A high-precision wireless mouse.");
//        goods.setPrice(new BigDecimal("1499.99"));
//        goods.setMainImageUrl("https://example.com/images/mouse.jpg");
//        goods.setStock(120);
//        goods.setReviewsCount(45);
//        goods.setShopId(UUID.randomUUID());
//        goods.setCategory(category);
//        goods.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
//        goods.setDiscountStatus(Goods.DiscountStatus.NONE);
//        goods.setDiscountValue(null);
//        goods.setRating(4);
//        goods.setSlug("wireless-mouse-123");
//        goods.setMetaTitle("Wireless Mouse - Best Price");
//        goods.setMetaDescription("High-quality wireless mouse at the best price.");
//        goods.setCreatedAt(Instant.now());
//        goods.setUpdatedAt(Instant.now());
//        goods.setAdditionalImages(new ArrayList<>());
//
//        goodsDTO = new GoodsDTO();
//        goodsDTO.setId(goodsId);
//        goodsDTO.setArt("ART-001");
//        goodsDTO.setName("Wireless Mouse");
//        goodsDTO.setDescription("A high-precision wireless mouse.");
//        goodsDTO.setPrice(new BigDecimal("1499.99"));
//        goodsDTO.setMainImageUrl("https://example.com/images/mouse.jpg");
//        goodsDTO.setStock(120);
//        goodsDTO.setReviewsCount(45);
//        goodsDTO.setShopId(goods.getShopId());
//        goodsDTO.setCategoryId(categoryId);
//        goodsDTO.setGoodsStatus("ACTIVE");
//        goodsDTO.setDiscountStatus("NONE");
//        goodsDTO.setDiscountValue(null);
//        goodsDTO.setRating(4);
//        goodsDTO.setSlug("wireless-mouse-123");
//        goodsDTO.setMetaTitle("Wireless Mouse - Best Price");
//        goodsDTO.setMetaDescription("High-quality wireless mouse at the best price.");
//        goodsDTO.setCreatedAt(goods.getCreatedAt());
//        goodsDTO.setUpdatedAt(goods.getUpdatedAt());
//        goodsDTO.setAdditionalImageUrls(new ArrayList<>());
//    }
//
//    @Test
//    @DisplayName("getAllGoods should return list of goods")
//    void getAllGoods_ShouldReturnListOfGoods() {
//        // Arrange
//        List<Goods> goodsList = Collections.singletonList(goods);
//        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);
//
//        when(goodsRepository.findAll()).thenReturn(goodsList);
//        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);
//
//        // Act
//        List<GoodsDTO> result = goodsService.getAllGoods();
//
//        // Assert
//        assertEquals(expectedDTOs, result);
//        verify(goodsRepository, times(1)).findAll();
//        verify(goodsMapper, times(1)).toDto(goods);
//    }
//
//    @Test
//    @DisplayName("getAllGoods should return empty list when no goods exist")
//    void getAllGoods_ShouldReturnEmptyList_WhenNoGoodsExist() {
//        // Arrange
//        when(goodsRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Act
//        List<GoodsDTO> result = goodsService.getAllGoods();
//
//        // Assert
//        assertTrue(result.isEmpty());
//        verify(goodsRepository, times(1)).findAll();
//        verify(goodsMapper, never()).toDto(any());
//    }
//
//    @Test
//    @DisplayName("getGoodsById should return goods when found")
//    void getGoodsById_ShouldReturnGoods_WhenFound() {
//        // Arrange
//        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
//        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);
//
//        // Act
//        GoodsDTO result = goodsService.getGoodsById(goodsId);
//
//        // Assert
//        assertEquals(goodsDTO, result);
//        verify(goodsRepository, times(1)).findById(goodsId);
//        verify(goodsMapper, times(1)).toDto(goods);
//    }
//
//    @Test
//    @DisplayName("getGoodsById should throw GoodsNotFoundException when not found")
//    void getGoodsById_ShouldThrowException_WhenNotFound() {
//        // Arrange
//        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(GoodsNotFoundException.class, () -> goodsService.getGoodsById(goodsId));
//        verify(goodsRepository, times(1)).findById(goodsId);
//        verify(goodsMapper, never()).toDto(any());
//    }
//
//    @Test
//    @DisplayName("createGoods should create and return new goods")
//    void createGoods_ShouldCreateAndReturnGoods() {
//        // Arrange
//        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(false);
//        when(goodsMapper.toEntity(goodsDTO)).thenReturn(goods);
//        when(goodsRepository.save(goods)).thenReturn(goods);
//        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);
//
//        // Act
//        GoodsDTO result = goodsService.createGoods(goodsDTO);
//
//        // Assert
//        assertEquals(goodsDTO, result);
//        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
//        verify(goodsMapper, times(1)).toEntity(goodsDTO);
//        verify(goodsRepository, times(1)).save(goods);
//        verify(goodsMapper, times(1)).toDto(goods);
//    }
//
//    @Test
//    @DisplayName("createGoods should throw IllegalArgumentException when art exists")
//    void createGoods_ShouldThrowException_WhenArtExists() {
//        // Arrange
//        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(true);
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> goodsService.createGoods(goodsDTO));
//        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
//        verify(goodsMapper, never()).toEntity(any());
//        verify(goodsRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("updateGoods should update and return updated goods")
//    void updateGoods_ShouldUpdateAndReturnGoods() {
//        // Arrange
//        GoodsDTO updatedDTO = new GoodsDTO();
//        updatedDTO.setId(goodsId);
//        updatedDTO.setArt("ART-002");
//        updatedDTO.setName("Updated Mouse");
//        updatedDTO.setPrice(new BigDecimal("1999.99"));
//        updatedDTO.setGoodsStatus("ACTIVE");
//        updatedDTO.setDiscountStatus("ACTIVE");
//
//        Goods updatedGoods = new Goods();
//        updatedGoods.setId(goodsId);
//        updatedGoods.setArt("ART-002");
//        updatedGoods.setName("Updated Mouse");
//        updatedGoods.setPrice(new BigDecimal("1999.99"));
//        updatedGoods.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
//        updatedGoods.setDiscountStatus(Goods.DiscountStatus.ACTIVE);
//
//        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
//        when(goodsRepository.existsByArt("ART-002")).thenReturn(false);
//        when(goodsMapper.toEntity(updatedDTO)).thenReturn(updatedGoods);
//        when(goodsRepository.save(updatedGoods)).thenReturn(updatedGoods);
//        when(goodsMapper.toDto(updatedGoods)).thenReturn(updatedDTO);
//
//        // Act
//        GoodsDTO result = goodsService.updateGoods(goodsId, updatedDTO);
//
//        // Assert
//        assertEquals(updatedDTO, result);
//        verify(goodsRepository, times(1)).findById(goodsId);
//        verify(goodsRepository, times(1)).existsByArt("ART-002");
//        verify(goodsMapper, times(1)).toEntity(updatedDTO);
//        verify(goodsRepository, times(1)).save(updatedGoods);
//        verify(goodsMapper, times(1)).toDto(updatedGoods);
//    }
//
//    @Test
//    @DisplayName("updateGoods should throw GoodsNotFoundException when not found")
//    void updateGoods_ShouldThrowException_WhenNotFound() {
//        // Arrange
//        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(GoodsNotFoundException.class, () -> goodsService.updateGoods(goodsId, goodsDTO));
//        verify(goodsRepository, times(1)).findById(goodsId);
//        verify(goodsRepository, never()).existsByArt(any());
//        verify(goodsMapper, never()).toEntity(any());
//    }
//
//    @Test
//    @DisplayName("deleteGoods should delete goods when found")
//    void deleteGoods_ShouldDeleteGoods_WhenFound() {
//        // Arrange
//        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
//        doNothing().when(goodsRepository).deleteById(goodsId);
//
//        // Act
//        goodsService.deleteGoods(goodsId);
//
//        // Assert
//        verify(goodsRepository, times(1)).findById(goodsId);
//        verify(goodsRepository, times(1)).deleteById(goodsId);
//    }
//
//    @Test
//    @DisplayName("deleteGoods should throw GoodsNotFoundException when not found")
//    void deleteGoods_ShouldThrowException_WhenNotFound() {
//        // Arrange
//        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(GoodsNotFoundException.class, () -> goodsService.deleteGoods(goodsId));
//        verify(goodsRepository, times(1)).findById(goodsId);
//        verify(goodsRepository, never()).deleteById(any());
//    }
//
//    @Test
//    @DisplayName("searchGoods should return matching goods")
//    void searchGoods_ShouldReturnMatchingGoods() {
//        // Arrange
//        List<Goods> goodsList = Collections.singletonList(goods);
//        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);
//
//        when(goodsRepository.findAll(any(Specification.class))).thenReturn(goodsList);
//        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);
//
//        // Act
//        List<GoodsDTO> result = goodsService.searchGoods(null, "ART-001", null, null, null, null, null, null, null, null, null);
//
//        // Assert
//        assertEquals(expectedDTOs, result);
//        verify(goodsRepository, times(1)).findAll(any(Specification.class));
//        verify(goodsMapper, times(1)).toDto(goods);
//    }
//
//    @Test
//    @DisplayName("searchGoods should return empty list when no matches")
//    void searchGoods_ShouldReturnEmptyList_WhenNoMatches() {
//        // Arrange
//        when(goodsRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());
//
//        // Act
//        List<GoodsDTO> result = goodsService.searchGoods(null, "NonExistent", null, null, null, null, null, null, null, null, null);
//
//        // Assert
//        assertTrue(result.isEmpty());
//        verify(goodsRepository, times(1)).findAll(any(Specification.class));
//        verify(goodsMapper, never()).toDto(any());
//    }
//}