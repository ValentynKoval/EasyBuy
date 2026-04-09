package com.teamchallenge.easybuy.service.shop.shoptaxinfo;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.domain.model.shop.ShopTaxInfo;
import com.teamchallenge.easybuy.dto.shop.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.exception.shop.ShopNotFoundException;
import com.teamchallenge.easybuy.mapper.shop.ShopTaxMapper;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.shop.shoptaxrepository.ShopTaxRepository;
import com.teamchallenge.easybuy.service.shop.security.ShopAccessGuard;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopTaxService {

    private final ShopTaxRepository taxRepository;
    private final ShopRepository shopRepository;
    private final ShopTaxMapper mapper;
    private final ShopAccessGuard accessGuard;

    // ===================== READ =====================

    @Transactional(readOnly = true)
    public ShopTaxInfoDTO getByShopId(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching tax info for shop: {}", shopId);

        return taxRepository.findById(shopId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Tax info not found for shop: " + shopId));
    }

    // ===================== CREATE =====================

    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            backoff = @Backoff(delay = 500)
    )
    public ShopTaxInfoDTO create(@NotNull UUID shopId, @Valid @NotNull ShopTaxInfoDTO dto) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Creating tax info for shop: {}", shopId);

        Shop shop = findShopOrThrow(shopId);

        if (taxRepository.existsById(shopId)) {
            throw new IllegalStateException("Tax info already exists for shop: " + shopId);
        }

        ShopTaxInfo entity = mapper.toEntity(dto);
        entity.setId(shopId);
        entity.setShop(shop);

        ShopTaxInfo saved = taxRepository.save(entity);

        log.info("Created tax info for shop: {}", shopId);
        return mapper.toDto(saved);
    }

    // ===================== UPDATE =====================

    public ShopTaxInfoDTO update(@NotNull UUID shopId, @Valid @NotNull ShopTaxInfoDTO dto) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Updating tax info for shop: {}", shopId);

        ShopTaxInfo entity = taxRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Tax info not found for shop: " + shopId));

        mapper.updateEntityFromDto(dto, entity);

        ShopTaxInfo updated = taxRepository.save(entity);

        log.info("Updated tax info for shop: {}", shopId);
        return mapper.toDto(updated);
    }

    // ===================== PATCH =====================

    public ShopTaxInfoDTO patch(@NotNull UUID shopId, @Valid @NotNull ShopTaxInfoDTO dto) {
        log.info("Patching tax info for shop: {}", shopId);
        return update(shopId, dto);
    }

    // ===================== DELETE =====================

    public void delete(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Deleting tax info for shop: {}", shopId);

        if (!taxRepository.existsById(shopId)) {
            throw new IllegalArgumentException("Tax info not found for shop: " + shopId);
        }

        taxRepository.deleteById(shopId);

        log.info("Deleted tax info for shop: {}", shopId);
    }

    // ===================== HELPERS =====================

    private Shop findShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found: " + shopId));
    }
}

