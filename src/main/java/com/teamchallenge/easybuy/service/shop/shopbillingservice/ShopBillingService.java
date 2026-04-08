package com.teamchallenge.easybuy.service.shop.shopbillingservice;

import com.stripe.exception.StripeException;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.domain.model.shop.ShopBillingInfo;
import com.teamchallenge.easybuy.dto.shop.shopbillinginfo.ShopBillingInfoDTO;
import com.teamchallenge.easybuy.mapper.shop.ShopBillingMapper;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.shop.shopbillinginfo.ShopBillingRepository;
import com.teamchallenge.easybuy.service.payment.StripeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopBillingService {

    private final ShopBillingRepository billingRepository;
    private final ShopRepository shopRepository;
    private final ShopBillingMapper billingMapper;
    private final StripeService stripeService;

    @Transactional(readOnly = true)
    public ShopBillingInfoDTO getBillingInfo(UUID shopId) {
        ShopBillingInfo billingInfo = billingRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("Billing info not found for shop: " + shopId));
        return billingMapper.toDto(billingInfo);
    }

    /**
     * Initializes the Stripe linking process for the store.
     */
    @Transactional
    public ShopBillingInfoDTO setupStripeOnboarding(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("Shop not found: " + shopId));

        // Initialize billing if it has not yet been created (Shared PK will be filled in automatically)
        ShopBillingInfo billingInfo = shop.getShopBillingInfo();
        if (billingInfo == null) {
            billingInfo = ShopBillingInfo.builder()
                    .shop(shop)
                    .billingEmail(shop.getSeller().getEmail())
                    .build();
            billingInfo = billingRepository.save(billingInfo);
        }

        try {
            // If you don't have a Stripe account yet
            if (billingInfo.getStripeAccountId() == null) {
                String stripeId = stripeService.createStripeAccount(billingInfo.getBillingEmail());
                billingInfo.setStripeAccountId(stripeId);
                log.info("Registered new Stripe account {} for shop {}", stripeId, shopId);
            }

            // Generating a link for KYC verification
            String onboardingUrl = stripeService.createOnboardingLink(billingInfo.getStripeAccountId());

            ShopBillingInfoDTO dto = billingMapper.toDto(billingRepository.save(billingInfo));
            dto.setOnboardingUrl(onboardingUrl); // Устанавливаем динамическую ссылку в DTO

            return dto;

        } catch (StripeException e) {
            log.error("Stripe integration failed for shop {}: {}", shopId, e.getMessage());
            throw new RuntimeException("Stripe service communication error", e);
        }
    }
}