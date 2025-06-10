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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {

    @Mock
    private GoodsRepository goodsRepository;

    @Mock
    private GoodsMapper goodsMapper;

    @InjectMocks
    private GoodsService goodsService;

    private Goods goods;
    private GoodsDTO goodsDTO;
    private UUID goodsId;
    private UUID categoryId;
    private Category category;

    @BeforeEach
    void setUp() {
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
        goods.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        goods.setDiscountStatus(Goods.DiscountStatus.NONE);
        goods.setDiscountValue(null);
        goods.setRating(4);
        goods.setSlug("wireless-mouse-123");
        goods.setMetaTitle("Wireless Mouse - Best Price");
        goods.setMetaDescription("High-quality wireless mouse at the best price.");
        goods.setCreatedAt(Instant.now());
        goods.setUpdatedAt(Instant.now());
        goods.setAdditionalImages(new ArrayList<>());

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
        goodsDTO.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        goodsDTO.setDiscountStatus(Goods.DiscountStatus.NONE);
        goodsDTO.setDiscountValue(null);
        goodsDTO.setRating(4);
        goodsDTO.setSlug("wireless-mouse-123");
        goodsDTO.setMetaTitle("Wireless Mouse - Best Price");
        goodsDTO.setMetaDescription("High-quality wireless mouse at the best price.");
        goodsDTO.setCreatedAt(goods.getCreatedAt());
        goodsDTO.setUpdatedAt(goods.getUpdatedAt());
        goodsDTO.setAdditionalImages(new ArrayList<>());
    }

    @Test
    @DisplayName("getAllGoods should return a list of goods DTOs")
    void getAllGoods_ShouldReturnListOfGoods() {
        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        when(goodsRepository.findAll()).thenReturn(goodsList);
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        List<GoodsDTO> result = goodsService.getAllGoods();

        assertEquals(expectedDTOs, result);
        verify(goodsRepository, times(1)).findAll();
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getAllGoods should return an empty list when no goods exist")
    void getAllGoods_ShouldReturnEmptyList_WhenNoGoodsExist() {
        when(goodsRepository.findAll()).thenReturn(Collections.emptyList());

        List<GoodsDTO> result = goodsService.getAllGoods();

        assertTrue(result.isEmpty());
        verify(goodsRepository, times(1)).findAll();
        verify(goodsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getGoodsById should return goods DTO when found")
    void getGoodsById_ShouldReturnGoods_WhenFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        GoodsDTO result = goodsService.getGoodsById(goodsId);

        assertEquals(goodsDTO, result);
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getGoodsById should throw GoodsNotFoundException when not found")
    void getGoodsById_ShouldThrowException_WhenNotFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.getGoodsById(goodsId));
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createGoods should create and return a new goods DTO")
    void createGoods_ShouldCreateAndReturnGoods() {
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(false);
        when(goodsMapper.toEntity(goodsDTO)).thenReturn(goods);
        when(goodsRepository.save(goods)).thenReturn(goods);
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        GoodsDTO result = goodsService.createGoods(goodsDTO);

        assertEquals(goodsDTO, result);
        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
        verify(goodsMapper, times(1)).toEntity(goodsDTO);
        verify(goodsRepository, times(1)).save(goods);
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("createGoods should throw IllegalArgumentException when goods ART already exists")
    void createGoods_ShouldThrowException_WhenArtExists() {
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> goodsService.createGoods(goodsDTO));
        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
        // ВИПРАВЛЕННЯ: toEntity принимает GoodsDTO, save принимает Goods
        verify(goodsMapper, never()).toEntity(any(GoodsDTO.class)); // <--- ИЗМЕНЕНИЕ: Указываем GoodsDTO.class
        verify(goodsRepository, never()).save(any(Goods.class)); // <--- ИЗМЕНЕНИЕ: Указываем Goods.class
    }

    @Test
    @DisplayName("updateGoods should update and return updated goods DTO")
    void updateGoods_ShouldUpdateAndReturnGoods() {
        GoodsDTO updatedDTO = new GoodsDTO();
        updatedDTO.setId(goodsId);
        updatedDTO.setArt("ART-002");
        updatedDTO.setName("Updated Mouse");
        updatedDTO.setDescription("A high-precision updated wireless mouse.");
        updatedDTO.setPrice(new BigDecimal("1999.99"));
        updatedDTO.setMainImageUrl("https://example.com/images/updated-mouse.jpg");
        updatedDTO.setStock(130);
        updatedDTO.setReviewsCount(50);
        updatedDTO.setShopId(goods.getShopId());
        updatedDTO.setCategoryId(categoryId);
        updatedDTO.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        updatedDTO.setDiscountStatus(Goods.DiscountStatus.ACTIVE);
        updatedDTO.setDiscountValue(new BigDecimal("100.00"));
        updatedDTO.setRating(5);
        updatedDTO.setSlug("updated-wireless-mouse-123");
        updatedDTO.setMetaTitle("Updated Wireless Mouse - Better Price");
        updatedDTO.setMetaDescription("High-quality updated wireless mouse at a better price.");
        updatedDTO.setCreatedAt(goods.getCreatedAt());
        updatedDTO.setUpdatedAt(Instant.now());
        updatedDTO.setAdditionalImages(new ArrayList<>());


        Goods updatedGoodsEntity = new Goods();
        updatedGoodsEntity.setId(goodsId);
        updatedGoodsEntity.setArt("ART-002");
        updatedGoodsEntity.setName("Updated Mouse");
        updatedGoodsEntity.setDescription("A high-precision updated wireless mouse.");
        updatedGoodsEntity.setPrice(new BigDecimal("1999.99"));
        updatedGoodsEntity.setMainImageUrl("https://example.com/images/updated-mouse.jpg");
        updatedGoodsEntity.setStock(130);
        updatedGoodsEntity.setReviewsCount(50);
        updatedGoodsEntity.setShopId(goods.getShopId());
        updatedGoodsEntity.setCategory(category);
        updatedGoodsEntity.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        updatedGoodsEntity.setDiscountStatus(Goods.DiscountStatus.ACTIVE);
        updatedGoodsEntity.setDiscountValue(new BigDecimal("100.00"));
        updatedGoodsEntity.setRating(5);
        updatedGoodsEntity.setSlug("updated-wireless-mouse-123");
        updatedGoodsEntity.setMetaTitle("Updated Wireless Mouse - Better Price");
        updatedGoodsEntity.setMetaDescription("High-quality updated wireless mouse at a better price.");
        updatedGoodsEntity.setCreatedAt(goods.getCreatedAt());
        updatedGoodsEntity.setUpdatedAt(Instant.now());
        updatedGoodsEntity.setAdditionalImages(new ArrayList<>());


        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsRepository.existsByArt(updatedDTO.getArt())).thenReturn(false);
        when(goodsMapper.toEntity(updatedDTO)).thenReturn(updatedGoodsEntity);
        when(goodsRepository.save(updatedGoodsEntity)).thenReturn(updatedGoodsEntity);
        when(goodsMapper.toDto(updatedGoodsEntity)).thenReturn(updatedDTO);

        GoodsDTO result = goodsService.updateGoods(goodsId, updatedDTO);

        assertEquals(updatedDTO, result);
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, times(1)).existsByArt(updatedDTO.getArt());
        verify(goodsMapper, times(1)).toEntity(updatedDTO);
        verify(goodsRepository, times(1)).save(updatedGoodsEntity);
        verify(goodsMapper, times(1)).toDto(updatedGoodsEntity);
    }

    @Test
    @DisplayName("updateGoods should throw GoodsNotFoundException when goods to update are not found")
    void updateGoods_ShouldThrowException_WhenNotFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.updateGoods(goodsId, goodsDTO));
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, never()).existsByArt(any());
        verify(goodsMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("updateGoods should throw IllegalArgumentException when new ART already exists for another goods")
    void updateGoods_ShouldThrowException_WhenNewArtExistsForOtherGoods() {
        GoodsDTO updatedDTOAttemptingExistingArt = new GoodsDTO();
        updatedDTOAttemptingExistingArt.setId(goodsId);
        updatedDTOAttemptingExistingArt.setArt("EXISTING-ART-IN-DB");
        updatedDTOAttemptingExistingArt.setName("Attempt to use existing ART");
        updatedDTOAttemptingExistingArt.setPrice(new BigDecimal("100.00"));
        updatedDTOAttemptingExistingArt.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        updatedDTOAttemptingExistingArt.setDiscountStatus(Goods.DiscountStatus.NONE);
        updatedDTOAttemptingExistingArt.setShopId(goods.getShopId());
        updatedDTOAttemptingExistingArt.setCategoryId(categoryId);
        updatedDTOAttemptingExistingArt.setMainImageUrl("http://some.url/img.jpg");
        updatedDTOAttemptingExistingArt.setAdditionalImages(new ArrayList<>());


        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsRepository.existsByArt("EXISTING-ART-IN-DB")).thenReturn(true);


        assertThrows(IllegalArgumentException.class, () -> goodsService.updateGoods(goodsId, updatedDTOAttemptingExistingArt));

        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, times(1)).existsByArt("EXISTING-ART-IN-DB");
        // ВИПРАВЛЕННЯ: toEntity принимает GoodsDTO, save принимает Goods
        verify(goodsMapper, never()).toEntity(any(GoodsDTO.class)); // <--- ИЗМЕНЕНИЕ: Указываем GoodsDTO.class
        verify(goodsRepository, never()).save(any(Goods.class)); // <--- ИЗМЕНЕНИЕ: Указываем Goods.class
    }


    @Test
    @DisplayName("deleteGoods should delete goods when found")
    void deleteGoods_ShouldDeleteGoods_WhenFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        doNothing().when(goodsRepository).deleteById(goodsId);

        goodsService.deleteGoods(goodsId);

        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, times(1)).deleteById(goodsId);
    }

    @Test
    @DisplayName("deleteGoods should throw GoodsNotFoundException when goods to delete are not found")
    void deleteGoods_ShouldThrowException_WhenNotFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.deleteGoods(goodsId));
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("searchGoods should return matching goods DTOs based on criteria")
    void searchGoods_ShouldReturnMatchingGoods() {
        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        // Уточняем тип дженерика для Specification
        when(goodsRepository.findAll(any(Specification.class))).thenReturn(goodsList);
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        List<GoodsDTO> result = goodsService.searchGoods(null, "ART-001", null, null, null, null, null, null, null, null, null);

        assertEquals(expectedDTOs, result);
        // Уточняем тип дженерика для Specification
        verify(goodsRepository, times(1)).findAll(any(Specification.class));
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("searchGoods should return an empty list when no matches are found")
    void searchGoods_ShouldReturnEmptyList_WhenNoMatches() {
        // Уточняем тип дженерика для Specification
        when(goodsRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<GoodsDTO> result = goodsService.searchGoods(null, "NonExistentArt", null, null, null, null, null, null, null, null, null);

        assertTrue(result.isEmpty());
        // Уточняем тип дженерика для Specification
        verify(goodsRepository, times(1)).findAll(any(Specification.class));
        verify(goodsMapper, never()).toDto(any());
    }
}