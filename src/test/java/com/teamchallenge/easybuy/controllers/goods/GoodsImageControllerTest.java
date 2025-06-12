package com.teamchallenge.easybuy.controllers.goods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.dto.goods.GoodsImageDTO;
import com.teamchallenge.easybuy.exceptions.goods.GoodsImageException;
import com.teamchallenge.easybuy.services.goods.image.GoodsImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.teamchallenge.easybuy.exceptions.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
class GoodsImageControllerTest {

    @Mock
    private GoodsImageService goodsImageService;

    @InjectMocks
    private GoodsImageController goodsImageController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private UUID id; // ID самого зображення
    private UUID goodsId; // ID товару

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(goodsImageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        id = UUID.randomUUID(); // Ініціалізуємо в setUp
        goodsId = UUID.randomUUID(); // Ініціалізуємо в setUp
    }

    @Test
    void getAllImages_shouldReturnList() throws Exception {
        GoodsImageDTO dto = new GoodsImageDTO();
        dto.setId(id);
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setGoodsId(goodsId);

        when(goodsImageService.getAllImages()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/goods-images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/image.jpg"));
    }

    @Test
    void getImageById_shouldReturnImage() throws Exception {
        GoodsImageDTO dto = new GoodsImageDTO();
        dto.setId(id);
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setGoodsId(goodsId);

        when(goodsImageService.getImageById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/goods-images/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"));
    }

    @Test
    void getImageById_shouldReturn404WhenNotFound() throws Exception {
        when(goodsImageService.getImageById(id))
                .thenThrow(new GoodsImageException("Image with ID " + id + " not found"));

        mockMvc.perform(get("/api/goods-images/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Image with ID " + id + " not found"));
    }

    @Test
    void createImage_shouldReturnCreatedImage() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        GoodsImageDTO dto = new GoodsImageDTO();
        dto.setId(UUID.randomUUID());
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setGoodsId(goodsId);

        // Мокування сервісу:
        // createImage(UUID goodsId, MultipartFile file)
        when(goodsImageService.createImage(eq(goodsId), any(MockMultipartFile.class))).thenReturn(dto);

        // Змінений HTTP-запит:
        // POST /api/goods-images  з @RequestParam("goodsId") та @RequestParam("file")
        mockMvc.perform(multipart("/api/goods-images") // URL тепер без goodsId у шляху
                        .file(mockFile) // Додаємо файл
                        .param("goodsId", goodsId.toString()) // Додаємо goodsId як параметр запиту
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.goodsId").value(goodsId.toString()));
    }

    @Test
    void updateImage_shouldReturnUpdatedImage() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "updated_test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "updated image content".getBytes()
        );

        GoodsImageDTO dto = new GoodsImageDTO();
        dto.setId(id);
        dto.setImageUrl("https://example.com/updated-image.jpg");
        dto.setGoodsId(goodsId);

        // Мокування сервісу:
        // updateImage(UUID id, UUID goodsId, MultipartFile file)
        when(goodsImageService.updateImage(eq(id), eq(goodsId), any(MockMultipartFile.class))).thenReturn(dto);

        // Змінений HTTP-запит:
        // PUT /api/goods-images/{id} з @RequestParam("goodsId") та @RequestParam("file")
        mockMvc.perform(multipart("/api/goods-images/{id}", id) // URL з id у шляху
                        .file(mockFile) // Додаємо файл
                        .param("goodsId", goodsId.toString()) // Додаємо goodsId як параметр запиту
                        .with(request -> {
                            request.setMethod("PUT"); // Змінюємо метод на PUT
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/updated-image.jpg"))
                .andExpect(jsonPath("$.goodsId").value(goodsId.toString()));
    }


    @Test
    void deleteImage_shouldReturn200() throws Exception {
        doNothing().when(goodsImageService).deleteImage(id);

        mockMvc.perform(delete("/api/goods-images/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void searchImages_shouldReturnFilteredList() throws Exception {
        GoodsImageDTO dto = new GoodsImageDTO();
        dto.setId(id);
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setGoodsId(goodsId);

        when(goodsImageService.searchImages(goodsId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/goods-images/search")
                        .param("goodsId", goodsId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/image.jpg"));
    }
}