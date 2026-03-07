package com.teamchallenge.easybuy.service.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import com.teamchallenge.easybuy.exception.Shop.ShopNotFoundException;
import com.teamchallenge.easybuy.mapper.shop.ShopMapper;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.shop.ShopSearchBuilder;
import com.teamchallenge.easybuy.repository.user.UserRepository;
import com.teamchallenge.easybuy.repository.user.seller.SellerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Service layer responsible for managing Shop entities.
 */
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    /**
     * GET ALL SHOPS
     */
    @Transactional(readOnly = true)
    public Page<ShopDTO> getAllShops(Pageable pageable) {

        return shopRepository.findAll(pageable)
                .map(shopMapper::toDto);
    }

    /**
     * GET SHOP BY ID
     */
    @Transactional(readOnly = true)
    public ShopDTO getShopById(UUID id) {

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ShopNotFoundException("Shop not found with id: " + id));

        return shopMapper.toDto(shop);
    }

    /**
     * CREATE SHOP
     */
    @Transactional
    public ShopDTO createShop(ShopDTO shopDTO) {

        validateShopNameUnique(shopDTO.getShopName());

        Shop shop = shopMapper.toEntity(shopDTO);

        setShopRelations(shop, shopDTO);

        try {
            shop = shopRepository.save(shop);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        }

        return shopMapper.toDto(shop);
    }

    /**
     * FULL UPDATE
     */
    @Transactional
    public ShopDTO updateShop(UUID id, ShopDTO shopDTO) {

        Shop existingShop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ShopNotFoundException("Shop not found with id: " + id));

        if (shopDTO.getShopName() != null &&
                !shopDTO.getShopName().equals(existingShop.getShopName())) {

            validateShopNameUnique(shopDTO.getShopName());
        }

        shopMapper.updateEntityFromDto(shopDTO, existingShop);

        setShopRelations(existingShop, shopDTO);

        return shopMapper.toDto(shopRepository.save(existingShop));
    }

    /**
     * PARTIAL UPDATE (PATCH)
     */
    @Transactional
    public ShopDTO patchShop(UUID id, Map<String, Object> updates) {

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ShopNotFoundException("Shop not found with id: " + id));

        updates.forEach((key, value) -> {

            switch (key) {

                case "shopName" -> {
                    String newName = value.toString();

                    if (!newName.equals(shop.getShopName())) {
                        validateShopNameUnique(newName);
                    }

                    shop.setShopName(newName);
                }

                case "shopDescription" ->
                        shop.setShopDescription(value.toString());

                case "isFeatured" ->
                        shop.setFeatured(Boolean.parseBoolean(value.toString()));

                case "shopStatus" ->
                        shop.setShopStatus(
                                Shop.ShopStatus.valueOf(value.toString())
                        );

                case "slug" ->
                        shop.setSlug(value.toString());

                case "currency" ->
                        shop.setCurrency(value.toString());

                case "language" ->
                        shop.setLanguage(value.toString());

                case "timezone" ->
                        shop.setTimezone(value.toString());

                case "commissionRate" ->
                        shop.setCommissionRate(
                                new BigDecimal(value.toString())
                        );

                case "shopType" ->
                        shop.setShopType(
                                Shop.ShopType.valueOf(value.toString())
                        );

                default ->
                        throw new IllegalArgumentException(
                                "Invalid field for update: " + key
                        );
            }

        });

        return shopMapper.toDto(shopRepository.save(shop));
    }

    /**
     * DELETE SHOP
     */
    @Transactional
    public void deleteShop(UUID id) {

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() ->
                        new ShopNotFoundException("Shop not found with id: " + id));

        shopRepository.delete(shop);
    }

    /**
     * SEARCH SHOPS
     */
    @Transactional(readOnly = true)
        public Page<ShopDTO> searchShops(ShopSearchParams params, Pageable pageable) {

            Specification<Shop> spec = ShopSearchBuilder
                    .builder()
                    .withParams(params)
                    .build();

            return shopRepository.findAll(spec, pageable)
                    .map(shopMapper::toDto);
        }


    /**
     * VALIDATE UNIQUE SHOP NAME
     */
    private void validateShopNameUnique(String shopName) {

        if (shopRepository.existsByShopName(shopName)) {
            throw new IllegalArgumentException(
                    "Shop name already exists: " + shopName
            );
        }
    }

    /**
     * SET ENTITY RELATIONS
     */
    private void setShopRelations(Shop shop, ShopDTO dto) {

        if (dto.getSellerId() != null) {

            shop.setSeller(
                    sellerRepository.findById(dto.getSellerId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Seller not found"))
            );
        }

        if (dto.getModeratedByUserId() != null) {

            shop.setModeratedByUser(
                    userRepository.findById(dto.getModeratedByUserId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Moderator not found"))
            );
        }
    }
}