package com.teamchallenge.easybuy.service.shop;

import com.teamchallenge.easybuy.domain.model.goods.Goods;
import com.teamchallenge.easybuy.domain.model.goods.category.Category;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.domain.model.user.Seller;
import com.teamchallenge.easybuy.domain.model.user.User;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import com.teamchallenge.easybuy.exception.DuplicateResourceException;
import com.teamchallenge.easybuy.exception.ResourceNotFoundException;
import com.teamchallenge.easybuy.mapper.shop.ShopMapper;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.user.seller.SellerRepository;
import com.teamchallenge.easybuy.repository.user.UserRepository;
import com.teamchallenge.easybuy.repository.shop.ShopSpecifications;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ShopDTO> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable)
                .map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ShopDTO getShopById(UUID id) {
        return shopRepository.findById(id)
                .map(shopMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id)); // Использование кастомных исключений [8]
    }

    @Transactional
    public ShopDTO createShop(ShopDTO shopDTO) {
        // Простая проверка уникальности через метод репозитория
        if (shopRepository.existsByShopName(shopDTO.getShopName())) {
            throw new DuplicateResourceException("Shop name already exists: " + shopDTO.getShopName());
        }

        Shop shop = shopMapper.toEntity(shopDTO);

        // Установка связей перед сохранением [9]
        setShopRelations(shop, shopDTO);

        return shopMapper.toDto(shopRepository.save(shop));
    }

    @Transactional
    public ShopDTO updateShop(UUID id, ShopDTO shopDTO) {
        Shop existingShop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        // Проверка уникальности имени при изменении
        if (shopDTO.getShopName() != null && !shopDTO.getShopName().equals(existingShop.getShopName())) {
            if (shopRepository.existsByShopName(shopDTO.getShopName())) {
                throw new DuplicateResourceException("Name already in use");
            }
        }

        // Синхронизация со всеми полями из Shop.java [1, 2]
        updateFields(existingShop, shopDTO);
        setShopRelations(existingShop, shopDTO);

        return shopMapper.toDto(shopRepository.save(existingShop));
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> searchShops(ShopSearchParams params, Pageable pageable) {
        Specification<Shop> spec = Specification.where(null);

        if (params.getShopName() != null && !params.getShopName().isBlank()) {
            spec = spec.and(ShopSpecifications.likeName(params.getShopName()));
        }

        if (params.getShopStatus() != null) {
            spec = spec.and(ShopSpecifications.hasStatus(params.getShopStatus()));
        }

        // Исправлено: использование обертки Boolean в параметрах для корректной проверки на null
        if (params.getIsFeatured() != null) {
            spec = spec.and(ShopSpecifications.isFeatured(params.getIsFeatured()));
        }

        if (params.getKeyword() != null && !params.getKeyword().isBlank()) {
            spec = spec.and(ShopSpecifications.likeNameOrDescription(params.getKeyword()));
        }

        return shopRepository.findAll(spec, pageable).map(shopMapper::toDto);
    }

    private void updateFields(Shop entity, ShopDTO dto) {
        if (dto.getShopName() != null) entity.setShopName(dto.getShopName());
        if (dto.getShopDescription() != null) entity.setShopDescription(dto.getShopDescription());
        if (dto.getShopStatus() != null) entity.setShopStatus(dto.getShopStatus());
        if (dto.getIsFeatured() != null) entity.setFeatured(dto.getIsFeatured());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getLanguage() != null) entity.setLanguage(dto.getLanguage());
        if (dto.getTimezone() != null) entity.setTimezone(dto.getTimezone());
        if (dto.getShopType() != null) entity.setShopType(dto.getShopType());
        // Добавьте остальные поля: commissionRate, isVerified и т.д. [2]
    }

    private void setShopRelations(Shop shop, ShopDTO dto) {
        if (dto.getSellerId() != null) {
            shop.setSeller(sellerRepository.findById(dto.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found")));
        }
        if (dto.getModeratedByUserId() != null) {
            shop.setModeratedByUser(userRepository.findById(dto.getModeratedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Moderator not found")));
        }
    }
}