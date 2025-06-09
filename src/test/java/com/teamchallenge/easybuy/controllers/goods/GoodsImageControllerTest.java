//package com.teamchallenge.easybuy.controllers.goods;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.teamchallenge.easybuy.dto.goods.GoodsImageDTO;
//import com.teamchallenge.easybuy.exceptions.goods.GoodsImageException;
//import com.teamchallenge.easybuy.services.goods.GoodsImageService;
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
//import java.util.List;
//import java.util.UUID;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class GoodsImageControllerTest {
//
//    @Mock
//    private GoodsImageService goodsImageService;
//
//    @InjectMocks
//    private GoodsImageController goodsImageController;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    private UUID id = UUID.randomUUID();
//    private UUID goodsId = UUID.randomUUID();
//
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//        mockMvc = MockMvcBuilders.standaloneSetup(goodsImageController).build();
//    }
//
//    @Test
//    void getAllImages_shouldReturnList() throws Exception {
//        GoodsImageDTO dto = new GoodsImageDTO();
//        dto.setId(id);
//        dto.setImageUrl("https://example.com/image.jpg");
//
//        when(goodsImageService.getAllImages()).thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/api/goods-images"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/image.jpg"));
//    }
//
//    @Test
//    void getImageById_shouldReturnImage() throws Exception {
//        GoodsImageDTO dto = new GoodsImageDTO();
//        dto.setId(id);
//        dto.setImageUrl("https://example.com/image.jpg");
//
//        when(goodsImageService.getImageById(id)).thenReturn(dto);
//
//        mockMvc.perform(get("/api/goods-images/{id}", id))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id.toString()))
//                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"));
//    }
//
//    @Test
//    void getImageById_shouldReturn404WhenNotFound() throws Exception {
//        when(goodsImageService.getImageById(id))
//                .thenThrow(new GoodsImageException("Image with ID " + id + " not found"));
//
//        mockMvc.perform(get("/api/goods-images/{id}", id))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.status").value(404));
//    }
//
//    @Test
//    void createImage_shouldReturnCreatedImage() throws Exception {
//        GoodsImageDTO dto = new GoodsImageDTO();
//        dto.setImageUrl("https://example.com/image.jpg");
//        dto.setGoodsId(goodsId);
//
//        when(goodsImageService.createImage(any(GoodsImageDTO.class))).thenReturn(dto);
//
//        mockMvc.perform(post("/api/goods-images")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"));
//    }
//
//    @Test
//    void updateImage_shouldReturnUpdatedImage() throws Exception {
//        GoodsImageDTO dto = new GoodsImageDTO();
//        dto.setId(id);
//        dto.setImageUrl("https://example.com/updated-image.jpg");
//
//        when(goodsImageService.updateImage(eq(id), any(GoodsImageDTO.class))).thenReturn(dto);
//
//        mockMvc.perform(put("/api/goods-images/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.imageUrl").value("https://example.com/updated-image.jpg"));
//    }
//
//    @Test
//    void deleteImage_shouldReturn200() throws Exception {
//        doNothing().when(goodsImageService).deleteImage(id);
//
//        mockMvc.perform(delete("/api/goods-images/{id}", id))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void searchImages_shouldReturnFilteredList() throws Exception {
//        GoodsImageDTO dto = new GoodsImageDTO();
//        dto.setId(id);
//        dto.setImageUrl("https://example.com/image.jpg");
//
//        when(goodsImageService.searchImages(goodsId)).thenReturn(List.of(dto));
//
//        mockMvc.perform(get("/api/goods-images/search")
//                        .param("goodsId", goodsId.toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/image.jpg"));
//    }
//}