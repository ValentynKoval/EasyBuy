//package com.teamchallenge.easybuy.controllers.goods;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
//import com.teamchallenge.easybuy.exceptions.GlobalExceptionHandler;
//import com.teamchallenge.easybuy.exceptions.goods.GoodsNotFoundException;
//import com.teamchallenge.easybuy.models.goods.Goods;
//import com.teamchallenge.easybuy.services.goods.GoodsService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class GoodsControllerTest {
//
//    @Mock
//    private GoodsService goodsService;
//
//    @InjectMocks
//    private GoodsController goodsController;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    private UUID id = UUID.randomUUID();
//    private UUID categoryId = UUID.randomUUID();
//
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//        mockMvc = MockMvcBuilders.standaloneSetup(goodsController)
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .build();
//    }
//
//    @Test
//    void getAllGoods_shouldReturnListWithFilters() throws Exception {
//        GoodsDTO dto = new GoodsDTO();
//        dto.setId(id);
//        dto.setName("Wireless Mouse");
//
//        when(goodsService.searchGoods(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/api/goods")
//                        .param("name", "Wireless Mouse"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].name").value("Wireless Mouse"));
//    }
//
//    @Test
//    void getGoodsById_shouldReturnGoods() throws Exception {
//        GoodsDTO dto = new GoodsDTO();
//        dto.setId(id);
//        dto.setName("Wireless Mouse");
//
//        when(goodsService.getGoodsById(id)).thenReturn(dto);
//
//        mockMvc.perform(get("/api/goods/{id}", id))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id.toString()))
//                .andExpect(jsonPath("$.name").value("Wireless Mouse"));
//    }
//
//    @Test
//    void getGoodsById_shouldReturn404WhenNotFound() throws Exception {
//        when(goodsService.getGoodsById(id))
//                .thenThrow(new GoodsNotFoundException(id));
//
//        mockMvc.perform(get("/api/goods/{id}", id))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404));
//    }
//
//    @Test
//    void createGoods_shouldReturnCreatedGoods() throws Exception {
//        GoodsDTO dto = new GoodsDTO();
//        dto.setName("Wireless Mouse");
//        dto.setArt("ART-001");
//        dto.setPrice(new BigDecimal("99.99"));
//        dto.setMainImageUrl("https://example.com/image.jpg");
//        dto.setStock(100);
//        dto.setCategoryId(categoryId);
//        dto.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
//        dto.setDiscountStatus(Goods.DiscountStatus.NONE);
//        dto.setShopId(UUID.randomUUID());
//
//        when(goodsService.createGoods(any(GoodsDTO.class))).thenReturn(dto);
//
//        mockMvc.perform(post("/api/goods")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("Wireless Mouse"));
//    }
//
//    @Test
//    void updateGoods_shouldReturnUpdatedGoods() throws Exception {
//        GoodsDTO dto = new GoodsDTO();
//        dto.setId(id);
//        dto.setName("Updated Mouse");
//
//        when(goodsService.updateGoods(eq(id), any(GoodsDTO.class))).thenReturn(dto);
//
//        mockMvc.perform(put("/api/goods/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Updated Mouse"));
//    }
//
//    @Test
//    void deleteGoods_shouldReturn200() throws Exception {
//        doNothing().when(goodsService).deleteGoods(id);
//
//        mockMvc.perform(delete("/api/goods/{id}", id))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void createGoods_shouldReturn400WhenInvalid() throws Exception {
//        GoodsDTO dto = new GoodsDTO();
//        dto.setName(null); // breaking @NotNull
//        dto.setArt(null);  // breaking @NotNull
//        dto.setPrice(new BigDecimal("-10")); // breaking @Positive
//        dto.setMainImageUrl("invalid-url");  // breaking @Pattern
//        dto.setStock(-5);  // breaking @PositiveOrZero
//        dto.setShopId(null); // breaking @NotNull
//        dto.setGoodsStatus(null); // breaking @NotNull
//        dto.setDiscountStatus(null); // breaking @NotNull
//
//        mockMvc.perform(post("/api/goods")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errors.name").value("must not be null"))
//                .andExpect(jsonPath("$.errors.price").value("must be greater than 0"));
//    }
//}