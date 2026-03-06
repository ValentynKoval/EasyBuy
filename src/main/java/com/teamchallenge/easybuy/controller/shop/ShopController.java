package com.teamchallenge.easybuy.controller.shop;

import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import com.teamchallenge.easybuy.service.shop.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing marketplace shops.
 * Provides endpoints for CRUD operations and advanced search.
 */
@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Tag(name = "Shop Management", description = "Endpoints for creating, updating, and searching marketplace shops")
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    @Operation(summary = "Get all shops", description = "Returns a paginated list of all registered shops.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<Page<ShopDTO>> getAllShops(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(shopService.getAllShops(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shop by ID", description = "Returns detailed information about a specific shop.")
    @ApiResponse(responseCode = "200", description = "Shop found")
    @ApiResponse(responseCode = "404", description = "Shop not found")
    public ResponseEntity<ShopDTO> getShopById(
            @Parameter(description = "UUID of the shop to retrieve") @PathVariable UUID id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new shop", description = "Registers a new shop in the marketplace.")
    @ApiResponse(responseCode = "201", description = "Shop successfully created")
    @ApiResponse(responseCode = "409", description = "Shop name already exists")
    public ResponseEntity<ShopDTO> createShop(@Valid @RequestBody ShopDTO shopDTO) {
        return new ResponseEntity<>(shopService.createShop(shopDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing shop", description = "Updates shop details based on the provided UUID.")
    @ApiResponse(responseCode = "200", description = "Shop successfully updated")
    @ApiResponse(responseCode = "404", description = "Shop not found")
    public ResponseEntity<ShopDTO> updateShop(
            @Parameter(description = "UUID of the shop to update") @PathVariable UUID id,
            @Valid @RequestBody ShopDTO shopDTO) {
        return ResponseEntity.ok(shopService.updateShop(id, shopDTO));
    }

    @GetMapping("/search")
    @Operation(summary = "Search shops", description = "Search and filter shops using multiple criteria (name, status, keyword).")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<Page<ShopDTO>> searchShops(
            @ModelAttribute ShopSearchParams params,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(shopService.searchShops(params, pageable));
    }
}
