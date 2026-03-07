package com.teamchallenge.easybuy.controller.shop;

import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import com.teamchallenge.easybuy.service.shop.ShopService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Tag(name = "Shop Management", description = "API for managing shops")
public class ShopController {

    private final ShopService shopService;

    /**
     * GET ALL SHOPS
     */
    @Operation(summary = "Get all shops")
    @GetMapping
    public ResponseEntity<Page<ShopDTO>> getAllShops(
            @PageableDefault(size = 20)
            @Parameter(hidden = true)
            Pageable pageable) {

        Page<ShopDTO> shops = shopService.getAllShops(pageable);
        return ResponseEntity.ok(shops);
    }

    /**
     * GET SHOP BY ID
     */
    @Operation(summary = "Get shop by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ShopDTO> getShopById(
            @PathVariable UUID id) {

        ShopDTO shop = shopService.getShopById(id);
        return ResponseEntity.ok(shop);
    }

    /**
     * CREATE SHOP
     */
    @Operation(summary = "Create a new shop")
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> createShop(
            @Valid @RequestBody ShopDTO shopDTO) {

        ShopDTO createdShop = shopService.createShop(shopDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdShop);
    }

    /**
     * FULL UPDATE SHOP
     */
    @Operation(summary = "Update shop (full update)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> updateShop(
            @PathVariable UUID id,
            @Valid @RequestBody ShopDTO shopDTO) {

        ShopDTO updatedShop = shopService.updateShop(id, shopDTO);
        return ResponseEntity.ok(updatedShop);
    }

    /**
     * PARTIAL UPDATE SHOP
     */
    @Operation(summary = "Partially update shop")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> patchShop(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {

        ShopDTO updatedShop = shopService.patchShop(id, updates);
        return ResponseEntity.ok(updatedShop);
    }

    /**
     * DELETE SHOP
     */
    @Operation(summary = "Delete shop")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShop(
            @PathVariable UUID id) {

        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * SEARCH SHOPS (Search Builder)
     */
    @Operation(summary = "Search shops with filters")
    @GetMapping("/search")
    public ResponseEntity<Page<ShopDTO>> searchShops(
            @ModelAttribute ShopSearchParams params,
            @PageableDefault(size = 20)
            @Parameter(hidden = true)
            Pageable pageable) {

        Page<ShopDTO> result = shopService.searchShops(params, pageable);
        return ResponseEntity.ok(result);
    }
}