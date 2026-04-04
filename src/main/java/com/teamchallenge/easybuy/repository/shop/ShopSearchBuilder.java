package com.teamchallenge.easybuy.repository.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import org.springframework.data.jpa.domain.Specification;

public class ShopSearchBuilder {

    private Specification<Shop> spec = Specification.where(null);

    public static ShopSearchBuilder builder() {
        return new ShopSearchBuilder();
    }

    public ShopSearchBuilder withParams(ShopSearchParams params) {

        if (params.getShopStatus() != null) {
            spec = spec.and(ShopSpecifications.hasStatus(params.getShopStatus()));
        }

        if (params.getIsFeatured() != null) {
            spec = spec.and(ShopSpecifications.isFeatured(params.getIsFeatured()));
        }

        if (params.getSellerId() != null) {
            spec = spec.and(ShopSpecifications.hasSellerId(params.getSellerId()));
        }

        if (params.getKeyword() != null && !params.getKeyword().isBlank()) {
            spec = spec.and(ShopSpecifications.textSearch(params.getKeyword()));
        }

        return this;
    }

    public Specification<Shop> build() {
        return spec;
    }
}