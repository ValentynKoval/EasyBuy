package com.teamchallenge.easybuy.repo.goods;


import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.models.goods.category.Category;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class GoodsSpecifications {

    public static Specification<Goods> hasId(UUID id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Goods> hasArt(String art) {
        return (root, query, cb) -> art == null ? null : cb.equal(root.get("art"), art);
    }

    public static Specification<Goods> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Goods> hasPrice(BigDecimal price) {
        return (root, query, cb) -> price == null ? null : cb.equal(root.get("price"), price);
    }

    public static Specification<Goods> hasStock(Integer stock) {
        return (root, query, cb) -> stock == null ? null : cb.equal(root.get("stock"), stock);
    }

    public static Specification<Goods> hasReviewsCount(Integer reviewsCount) {
        return (root, query, cb) -> reviewsCount == null ? null : cb.equal(root.get("reviewsCount"), reviewsCount);
    }

    public static Specification<Goods> hasShopId(UUID shopId) {
        return (root, query, cb) -> shopId == null ? null : cb.equal(root.get("shopId"), shopId);
    }

    public static Specification<Goods> hasCategory(Category category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Goods> hasGoodsStatus(Goods.GoodsStatus goodsStatus) {
        return (root, query, cb) -> goodsStatus == null ? null : cb.equal(root.get("goodsStatus"), goodsStatus);
    }

    public static Specification<Goods> hasDiscountStatus(Goods.DiscountStatus discountStatus) {
        return (root, query, cb) -> discountStatus == null ? null : cb.equal(root.get("discountStatus"), discountStatus);
    }

    public static Specification<Goods> hasRating(Integer rating) {
        return (root, query, cb) -> rating == null ? null : cb.equal(root.get("rating"), rating);
    }

    public static Specification<Goods> hasPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            return min != null ? cb.greaterThanOrEqualTo(root.get("price"), min)
                    : cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    public static Specification<Goods> hasSlugLike(String slug) {
        return (root, query, cb) -> slug == null ? null : cb.like(root.get("slug"), "%" + slug + "%");
    }

    public static Specification<Goods> createdAtBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("createdAt"), from, to);
            return from != null ? cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                    : cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    public static Specification<Goods> updatedAtBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("updatedAt"), from, to);
            return from != null ? cb.greaterThanOrEqualTo(root.get("updatedAt"), from)
                    : cb.lessThanOrEqualTo(root.get("updatedAt"), to);
        };
    }
    public static Specification<Goods> hasCategoryIn(Category... categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.length == 0) return null;
            return root.get("category").in(categories);
        };
    }

}