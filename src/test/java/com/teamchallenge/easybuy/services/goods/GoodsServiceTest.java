package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.exceptions.GoodsNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.GoodsMapper;
import com.teamchallenge.easybuy.models.goods.Goods;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GoodsService.
 */
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

    @BeforeEach
    void setUp() {
        goodsId = UUID.randomUUID();
        goods = Goods.builder()
                .id(goodsId)
                .art("ART-001")
                .name("Wireless Mouse")
                .description("A high-precision wireless mouse.")
                .price(new BigDecimal("1499.99"))
                .stock(120)
                .shopId(UUID.randomUUID())
                .goodsStatus(Goods.GoodsStatus.ACTIVE)
                .discountStatus(Goods.DiscountStatus.NONE)
                .discountValue(null)
                .build();

        goodsDTO = new GoodsDTO();
        goodsDTO.setId(goodsId);
        goodsDTO.setArt("ART-001");
        goodsDTO.setName("Wireless Mouse");
        goodsDTO.setDescription("A high-precision wireless mouse.");
        goodsDTO.setPrice(new BigDecimal("1499.99"));
        goodsDTO.setStock(120);
        goodsDTO.setShopId(goods.getShopId());
        goodsDTO.setGoodsStatus("ACTIVE"); // Уже строка
        goodsDTO.setDiscountStatus("NONE"); // Уже строка
        goodsDTO.setDiscountValue(null);
    }

    @Test
    @DisplayName("getAllGoods should return list of goods")
    void getAllGoods_ShouldReturnListOfGoods() {
        // Arrange
        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        when(goodsRepository.findAll()).thenReturn(goodsList);
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act
        List<GoodsDTO> result = goodsService.getAllGoods();

        // Assert
        assertEquals(expectedDTOs, result);
        verify(goodsRepository, times(1)).findAll();
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getGoodsById should return goods when found")
    void getGoodsById_ShouldReturnGoods_WhenFound() {
        // Arrange
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act
        GoodsDTO result = goodsService.getGoodsById(goodsId);

        // Assert
        assertEquals(goodsDTO, result);
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getGoodsById should throw GoodsNotFoundException when not found")
    void getGoodsById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GoodsNotFoundException.class, () -> goodsService.getGoodsById(goodsId));
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createGoods should create and return new goods")
    void createGoods_ShouldCreateAndReturnGoods() {
        // Arrange
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(false);
        when(goodsMapper.toEntity(goodsDTO)).thenReturn(goods);
        when(goodsRepository.save(goods)).thenReturn(goods);
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act
        GoodsDTO result = goodsService.createGoods(goodsDTO);

        // Assert
        assertEquals(goodsDTO, result);
        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
        verify(goodsMapper, times(1)).toEntity(goodsDTO);
        verify(goodsRepository, times(1)).save(goods);
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("createGoods should throw IllegalArgumentException when art exists")
    void createGoods_ShouldThrowException_WhenArtExists() {
        // Arrange
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> goodsService.createGoods(goodsDTO));
        verify(goodsRepository, times(1)).existsByArt(goodsDTO.getArt());
        verify(goodsMapper, never()).toEntity(any());
        verify(goodsRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateGoods should update and return updated goods")
    void updateGoods_ShouldUpdateAndReturnGoods() {
        // Arrange
        Goods updatedGoods = Goods.builder()
                .id(goodsId)
                .art("ART-002")
                .name("Updated Mouse")
                .description("Updated description.")
                .price(new BigDecimal("1999.99"))
                .stock(100)
                .shopId(goods.getShopId())
                .goodsStatus(Goods.GoodsStatus.ACTIVE)
                .discountStatus(Goods.DiscountStatus.ACTIVE)
                .discountValue(new BigDecimal("10.00"))
                .build();

        GoodsDTO updatedDTO = new GoodsDTO();
        updatedDTO.setId(goodsId);
        updatedDTO.setArt("ART-002");
        updatedDTO.setName("Updated Mouse");
        updatedDTO.setDescription("Updated description.");
        updatedDTO.setPrice(new BigDecimal("1999.99"));
        updatedDTO.setStock(100);
        updatedDTO.setShopId(goods.getShopId());
        updatedDTO.setGoodsStatus("ACTIVE");
        updatedDTO.setDiscountStatus("ACTIVE");
        updatedDTO.setDiscountValue(new BigDecimal("10.00"));

        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsRepository.existsByArt("ART-002")).thenReturn(false);
        when(goodsMapper.toEntity(updatedDTO)).thenReturn(updatedGoods);
        when(goodsRepository.save(updatedGoods)).thenReturn(updatedGoods);
        when(goodsMapper.toDto(updatedGoods)).thenReturn(updatedDTO);

        // Act
        GoodsDTO result = goodsService.updateGoods(goodsId, updatedDTO);

        // Assert
        assertEquals(updatedDTO, result);
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, times(1)).existsByArt("ART-002");
        verify(goodsMapper, times(1)).toEntity(updatedDTO);
        verify(goodsRepository, times(1)).save(updatedGoods);
        verify(goodsMapper, times(1)).toDto(updatedGoods);
    }

    @Test
    @DisplayName("updateGoods should throw GoodsNotFoundException when not found")
    void updateGoods_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GoodsNotFoundException.class, () -> goodsService.updateGoods(goodsId, goodsDTO));
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, never()).existsByArt(any());
        verify(goodsMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("deleteGoods should delete goods when found")
    void deleteGoods_ShouldDeleteGoods_WhenFound() {
        // Arrange
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        doNothing().when(goodsRepository).deleteById(goodsId);

        // Act
        goodsService.deleteGoods(goodsId);

        // Assert
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, times(1)).deleteById(goodsId);
    }

    @Test
    @DisplayName("deleteGoods should throw GoodsNotFoundException when not found")
    void deleteGoods_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GoodsNotFoundException.class, () -> goodsService.deleteGoods(goodsId));
        verify(goodsRepository, times(1)).findById(goodsId);
        verify(goodsRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("searchGoods should return goods matching search criteria")
    void searchGoods_ShouldReturnMatchingGoods() {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("name", "Mouse");
        searchParams.put("goodsstatus", "ACTIVE");

        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        when(goodsRepository.findAll(any(Specification.class))).thenReturn(goodsList);
        when(goodsMapper.toDto(goods)).thenReturn(goodsDTO);

        // Act
        List<GoodsDTO> result = goodsService.searchGoods(searchParams);

        // Assert
        assertEquals(expectedDTOs, result);
        verify(goodsRepository, times(1)).findAll(any(Specification.class));
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("searchGoods should return empty list when no matches")
    void searchGoods_ShouldReturnEmptyList_WhenNoMatches() {
        // Arrange
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("name", "NonExistent");

        when(goodsRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        // Act
        List<GoodsDTO> result = goodsService.searchGoods(searchParams);

        // Assert
        assertTrue(result.isEmpty());
        verify(goodsRepository, times(1)).findAll(any(Specification.class));
        verify(goodsMapper, never()).toDto(any());
    }
}