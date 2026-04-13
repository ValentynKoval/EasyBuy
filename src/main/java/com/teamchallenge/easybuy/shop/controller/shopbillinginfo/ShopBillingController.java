package com.teamchallenge.easybuy.shop.controller.shopbillinginfo;


import com.teamchallenge.easybuy.shop.dto.shopbillinginfo.ShopBillingInfoDTO;
import com.teamchallenge.easybuy.shop.service.shopbillingservice.ShopBillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing shop billing information and Stripe Connect onboarding.
 */
@RestController
@RequestMapping("/api/v1/shops/{shopId}/billing")
@RequiredArgsConstructor
@Tag(name = "Shop Billing", description = "Endpoints for managing shop Stripe integration and payouts")
public class ShopBillingController {

    private final ShopBillingService billingService;

    /**
     * Retrieves the current billing status and configuration for a specific shop.
     */
    @GetMapping
    @Operation(summary = "Get shop billing info",
            description = "Returns Stripe account status and billing email for the shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Billing info retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Billing info or shop not found")
    })
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopBillingInfoDTO> getBillingInfo(@PathVariable UUID shopId) {
        return ResponseEntity.ok(billingService.getBillingInfo(shopId));
    }

    /**
     * Initiates or resumes the Stripe onboarding process.
     * Returns a DTO containing a fresh onboardingUrl to redirect the seller.
     */
    @PostMapping("/onboarding")
    @Operation(summary = "Setup Stripe onboarding",
            description = "Creates a Stripe Connect account if missing and generates a fresh onboarding link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding link generated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopBillingInfoDTO> setupOnboarding(@PathVariable UUID shopId) {
        return ResponseEntity.ok(billingService.setupStripeOnboarding(shopId));
    }
}
