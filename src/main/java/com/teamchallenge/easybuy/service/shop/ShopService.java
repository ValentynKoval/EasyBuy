package com.teamchallenge.easybuy.service.shop;

import com.teamchallenge.easybuy.domain.events.ShopCreatedEvent;
import com.teamchallenge.easybuy.domain.events.ShopDeletedEvent;
import com.teamchallenge.easybuy.domain.events.ShopUpdatedEvent;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import com.teamchallenge.easybuy.exception.Shop.ShopNotFoundException;
import com.teamchallenge.easybuy.mapper.shop.ShopMapper;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.shop.ShopSearchBuilder;
import com.teamchallenge.easybuy.repository.user.UserRepository;
import com.teamchallenge.easybuy.repository.user.seller.SellerRepository;
import com.teamchallenge.easybuy.service.shop.validation.ShopValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 🏪 Clean and maintainable Shop Service
 *
 * Responsibilities:
 * - CRUD operations for shops
 * - Basic business validation
 * - Event publishing for side effects
 *
 * What this service does NOT do:
 * - Security (handled in controller layer)
 * - Complex analytics (separate service)
 * - Email notifications (event listeners)
 * - Caching (repository/infrastructure layer)
 * - File processing (separate service)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ShopValidationService validationService;
    private final ApplicationEventPublisher eventPublisher;

    // === READ OPERATIONS ===

    @Transactional(readOnly = true)
    public Page<ShopDTO> getAllShops(Pageable pageable) {
        log.debug("Fetching shops with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return shopRepository.findAll(pageable).map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ShopDTO getShopById(@NotNull UUID id) {
        log.debug("Fetching shop by ID: {}", id);
        return shopMapper.toDto(findShopOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> searchShops(@NotNull ShopSearchParams params, @NotNull Pageable pageable) {
        log.debug("Searching shops with params: {}", params);
        Specification<Shop> spec = ShopSearchBuilder.builder().withParams(params).build();
        return shopRepository.findAll(spec, pageable).map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> getShopsBySeller(@NotNull UUID sellerId, @NotNull Pageable pageable) {
        log.debug("Fetching shops for seller: {}", sellerId);
        return shopRepository.findBySellerId(sellerId, pageable).map(shopMapper::toDto);
    }

    // === WRITE OPERATIONS ===

    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public ShopDTO createShop(@Valid @NotNull ShopDTO shopDTO) {
        log.info("Creating shop: {}", shopDTO.getShopName());
        validationService.validateForCreation(shopDTO);

        Shop shop = shopMapper.toEntity(shopDTO);
        setShopRelations(shop, shopDTO);
        setDefaultsForNewShop(shop);

        shop = shopRepository.save(shop);
        eventPublisher.publishEvent(new ShopCreatedEvent(shop));

        log.info("Created shop: {} with ID: {}", shop.getShopName(), shop.getShopId());
        return shopMapper.toDto(shop);
    }

    public ShopDTO updateShop(@NotNull UUID id, @Valid @NotNull ShopDTO shopDTO) {
        log.info("Updating shop: {}", id);
        Shop existingShop = findShopOrThrow(id);
        validationService.validateForUpdate(existingShop, shopDTO);

        shopMapper.updateEntityFromDto(shopDTO, existingShop);
        setShopRelations(existingShop, shopDTO);

        Shop updatedShop = shopRepository.save(existingShop);
        eventPublisher.publishEvent(new ShopUpdatedEvent(updatedShop));

        log.info("Updated shop: {}", id);
        return shopMapper.toDto(updatedShop);
    }

    public ShopDTO patchShop(@NotNull UUID id, @NotNull @Valid ShopDTO updates) {
        // Optional partial update method
        return updateShop(id, updates);
    }

    public void deleteShop(@NotNull UUID id) {
        log.info("Deleting shop: {}", id);
        Shop shop = findShopOrThrow(id);
        validationService.validateForDeletion(shop);

        shopRepository.delete(shop);
        eventPublisher.publishEvent(new ShopDeletedEvent(shop));

        log.info("Deleted shop: {}", id);
    }

    // === UTILITY OPERATIONS ===

    @Transactional(readOnly = true)
    public boolean existsByName(@NotNull String shopName) {
        return shopRepository.existsByShopName(shopName.trim());
    }

    @Transactional(readOnly = true)
    public boolean existsBySlug(@NotNull String slug) {
        return shopRepository.existsBySlug(slug.trim().toLowerCase());
    }

    public String generateSlug(@NotNull String shopName) {
        String baseSlug = normalizeSlug(shopName);

        if (!existsBySlug(baseSlug)) return baseSlug;

        int counter = 1;
        String candidateSlug;
        do {
            candidateSlug = baseSlug + "-" + counter++;
        } while (existsBySlug(candidateSlug));

        return candidateSlug;
    }

    // === PRIVATE HELPERS ===

    private Shop findShopOrThrow(UUID id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found: " + id));
    }

    private void setShopRelations(Shop shop, ShopDTO dto) {
        if (dto.getSellerId() != null) {
            shop.setSeller(sellerRepository.findById(dto.getSellerId())
                    .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + dto.getSellerId())));
        }

        if (dto.getModeratedByUserId() != null) {
            shop.setModeratedByUser(userRepository.findById(dto.getModeratedByUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Moderator not found: " + dto.getModeratedByUserId())));
        }
    }

    private void setDefaultsForNewShop(Shop shop) {
        if (shop.getShopStatus() == null) shop.setShopStatus(Shop.ShopStatus.PENDING);
        if (shop.getShopType() == null) shop.setShopType(Shop.ShopType.RETAILER);
        if (!StringUtils.hasText(shop.getSlug()) && StringUtils.hasText(shop.getShopName())) {
            shop.setSlug(generateSlug(shop.getShopName()));
        }
    }

    private String normalizeSlug(String input) {
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}