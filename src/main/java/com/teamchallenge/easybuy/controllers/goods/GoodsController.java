package com.teamchallenge.easybuy.controllers.goods;
import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.services.goods.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goods")
@Tag(name = "Goods", description = "API for managing goods")
public class GoodsController {

    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping
    @Operation(summary = "Get all goods with optional filters", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class)))
    })
    public ResponseEntity<List<GoodsDTO>> getAllGoods(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String art,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Integer reviewsCount,
            @RequestParam(required = false) UUID shopId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Goods.GoodsStatus goodsStatus,
            @RequestParam(required = false) Goods.DiscountStatus discountStatus,
            @RequestParam(required = false) Integer rating) {
        Goods goodsFilter = new Goods();
        goodsFilter.setId(id);
        goodsFilter.setArt(art);
        goodsFilter.setName(name);
        goodsFilter.setPrice(price);
        goodsFilter.setStock(stock);
        goodsFilter.setReviewsCount(reviewsCount);
        goodsFilter.setShopId(shopId);
        goodsFilter.setCategory(categoryId != null ? new Category() {{ setId(categoryId); }} : null);
        goodsFilter.setGoodsStatus(goodsStatus);
        goodsFilter.setDiscountStatus(discountStatus);
        goodsFilter.setRating(rating);

        return ResponseEntity.ok(goodsService.searchGoods(
                id, art, name, price, stock, reviewsCount, shopId,
                goodsFilter.getCategory(), goodsStatus, discountStatus, rating));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goods by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goods not found")
    })
    public ResponseEntity<GoodsDTO> getGoodsById(@PathVariable UUID id) {
        return ResponseEntity.ok(goodsService.getGoodsById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new goods", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully created",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class)))
    })
    public ResponseEntity<GoodsDTO> createGoods(@RequestBody GoodsDTO dto) {
        return ResponseEntity.status(201).body(goodsService.createGoods(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goods", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goods not found")
    })
    public ResponseEntity<GoodsDTO> updateGoods(@PathVariable UUID id, @RequestBody GoodsDTO dto) {
        return ResponseEntity.ok(goodsService.updateGoods(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goods", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Goods not found")
    })
    public ResponseEntity<Void> deleteGoods(@PathVariable UUID id) {
        goodsService.deleteGoods(id);
        return ResponseEntity.ok().build();
    }
}