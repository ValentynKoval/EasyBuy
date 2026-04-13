package com.teamchallenge.easybuy.service.shop.validation;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopValidationService {

    private final ShopRepository shopRepository;

    public void validateForCreation(ShopDTO dto) {
        validateShopName(dto.getShopName());
        validateBusinessRules(dto);
    }

    public void validateForUpdate(Shop existing, ShopDTO dto) {
        if (isNameChanged(existing, dto)) {
            validateShopName(dto.getShopName());
        }
        validateBusinessRules(dto);
    }

    public void validateForDeletion(Shop shop) {
        if (shop.getGoods() != null && !shop.getGoods().isEmpty()) {
            throw new IllegalStateException("Cannot delete shop with goods");
        }
    }

    private void validateShopName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Shop name must not be blank");
        }

        if (shopRepository.existsByShopNameIgnoreCase(name.trim())) {
            throw new IllegalArgumentException("Shop name already exists: " + name);
        }
    }

    private void validateBusinessRules(ShopDTO dto) {
        // Add specific business rules here
    }

    private boolean isNameChanged(Shop existing, ShopDTO dto) {
        return dto.getShopName() != null &&
                !dto.getShopName().equals(existing.getShopName());
    }
}