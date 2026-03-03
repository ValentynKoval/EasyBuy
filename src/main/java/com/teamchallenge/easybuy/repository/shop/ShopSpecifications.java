package com.teamchallenge.easybuy.repository.shop;

import com.teamchallenge.easybuy.domain.model.goods.Goods;
import com.teamchallenge.easybuy.domain.model.goods.category.Category;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Utility class providing Spring Data JPA {@link Specification}s for filtering {@link Shop} entities.
 * Includes filters for status, featured flag, seller ID, and text-based search.
 */
public final class ShopSpecifications {

    private ShopSpecifications() {}

    /**
     * Filters shops by their current status (e.g., ACTIVE, INACTIVE).
     * @param status The shop status; ignored if null.
     */
    public static Specification<Shop> hasStatus(Shop.ShopStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("shopStatus"), status);
    }

    /**
     * Filters shops by their featured status.
     * @param featured True for featured shops; ignored if null.
     */
    public static Specification<Shop> isFeatured(Boolean featured) {
        return (root, query, cb) ->
                featured == null ? null :
                        cb.equal(root.get("isFeatured"), featured);
    }

    /**
     * Filters shops belonging to a specific seller ID.
     * @param sellerId The UUID of the seller; ignored if null.
     */
    public static Specification<Shop> hasSellerId(UUID sellerId) {
        return (root, query, cb) -> {
            if (sellerId == null) return null;
            return cb.equal(root.get("seller").get("id"), sellerId);
        };
    }

    /**
     * Performs a case-insensitive search in shop name and description.
     * @param keyword The search term; ignored if null or blank.
     */
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

    /**
     * Filters shops that have at least one good in the specified category.
     * @param categoryId The UUID of the category; ignored if null.
     */
    public static Specification<Shop> hasCategory(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;

            Join<Shop, Goods> goodsJoin = root.join("goods", JoinType.LEFT);
            query.distinct(true);

            return cb.equal(goodsJoin.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Shop> hasGoodsInSubcategory(UUID subcategoryId) {
        return (root, query, cb) -> {
            if (subcategoryId == null) {
                return cb.conjunction(); // Если ID не указан, игнорируем фильтр
            }
            Join<Shop, Goods> goodsJoin = root.join("goods", JoinType.LEFT);
            Join<Goods, Category> categoryJoin = goodsJoin.join("category", JoinType.LEFT);
            return cb.equal(categoryJoin.get("id"), subcategoryId);
        };
    }
    }