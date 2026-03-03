package com.teamchallenge.easybuy.repository.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class ShopSpecifications {

    private ShopSpecifications() {}

    public static Specification<Shop> hasStatus(Shop.ShopStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("shopStatus"), status);
    }

    public static Specification<Shop> isFeatured(Boolean featured) {
        return (root, query, cb) ->
                featured == null ? null :
                        cb.equal(root.get("isFeatured"), featured);
    }

    public static Specification<Shop> hasSellerId(UUID sellerId) {
        return (root, query, cb) -> {
            if (sellerId == null) return null;
            return cb.equal(root.get("seller").get("id"), sellerId);
        };
    }

    public static Specification<Shop> textSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("shopName")), pattern),
                    cb.like(cb.lower(root.get("shopDescription")), pattern)
            );
        };
    }
}