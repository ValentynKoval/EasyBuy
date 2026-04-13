package com.teamchallenge.easybuy.shop.repository;

import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.shop.entity.Shop;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Utility class providing reusable JPA Specifications for filtering {@link Shop} entities.
 */
public final class ShopSpecifications {

    private ShopSpecifications() {
        // Prevent instantiation of utility class
    }

    /**
     * Filters shops based on their operational status.
     *
     * @param status the status to filter by (e.g., ACTIVE, INACTIVE)
     * @return a specification that matches the given status, or a conjunction if status is null.
     */
    public static Specification<Shop> hasStatus(Shop.ShopStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("shopStatus"), status);
    }

    /**
     * Filters shops by their unique identifier.
     *
     * @param shopId the unique shop identifier.
     * @return a specification matching the provided shop ID.
     */
    public static Specification<Shop> hasShopId(UUID shopId) {
        return (root, query, cb) ->
                shopId == null ? cb.conjunction() : cb.equal(root.get("shopId"), shopId);
    }

    /**
     * Filters shops based on whether they are featured/recommended.
     *
     * @param featured true to find featured shops, false otherwise.
     * @return a specification for the featured flag.
     */
    public static Specification<Shop> isFeatured(Boolean featured) {
        return (root, query, cb) ->
                featured == null ? cb.conjunction() : cb.equal(root.get("isFeatured"), featured);
    }

    /**
     * Filters shops belonging to a specific seller.
     *
     * @param sellerId the unique identifier of the shop owner.
     * @return a specification that joins the 'seller' attribute and matches the ID.
     */
    public static Specification<Shop> hasSellerId(UUID sellerId) {
        return (root, query, cb) -> {
            if (sellerId == null) return cb.conjunction();
            return cb.equal(root.get("seller").get("id"), sellerId);
        };
    }

    /**
     * Filters shops moderated by a specific user.
     *
     * @param moderatedByUserId moderator user identifier.
     * @return a specification matching the moderator ID.
     */
    public static Specification<Shop> hasModeratedByUserId(UUID moderatedByUserId) {
        return (root, query, cb) ->
                moderatedByUserId == null ? cb.conjunction() : cb.equal(root.get("moderatedByUser").get("id"), moderatedByUserId);
    }

    /**
     * Performs a global case-insensitive search across shop names and descriptions.
     *
     * @param keyword the search term.
     * @return a specification that checks if the keyword is contained in name or description.
     */
    public static Specification<Shop> textSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();

            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("shopName")), pattern),
                    cb.like(cb.lower(root.get("shopDescription")), pattern)
            );
        };
    }

    /**
     * Filters shops by name using a partial match (LIKE).
     *
     * @param name the name or fragment of the name to search for.
     * @return a specification for partial name matching.
     */
    public static Specification<Shop> likeName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("shopName")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Filters shops by description using a partial match (LIKE).
     *
     * @param description the description fragment to search for.
     * @return a specification for partial description matching.
     */
    public static Specification<Shop> likeDescription(String description) {
        return (root, query, cb) -> {
            if (description == null || description.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("shopDescription")), "%" + description.toLowerCase() + "%");
        };
    }

    /**
     * Filters shops by exact slug (case-insensitive).
     *
     * @param slug the slug to match.
     * @return a specification for slug matching.
     */
    public static Specification<Shop> hasSlug(String slug) {
        return (root, query, cb) -> {
            if (slug == null || slug.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("slug")), slug.toLowerCase());
        };
    }

    /**
     * Filters shops created at or after the provided timestamp.
     *
     * @param minCreatedAt lower bound for createdAt.
     * @return a specification for createdAt lower bound.
     */
    public static Specification<Shop> createdAtAfterOrEqual(java.time.Instant minCreatedAt) {
        return (root, query, cb) ->
                minCreatedAt == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("createdAt"), minCreatedAt);
    }

    /**
     * Filters shops created at or before the provided timestamp.
     *
     * @param maxCreatedAt upper bound for createdAt.
     * @return a specification for createdAt upper bound.
     */
    public static Specification<Shop> createdAtBeforeOrEqual(java.time.Instant maxCreatedAt) {
        return (root, query, cb) ->
                maxCreatedAt == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("createdAt"), maxCreatedAt);
    }

    /**
     * Filters shops updated at or after the provided timestamp.
     *
     * @param minUpdatedAt lower bound for updatedAt.
     * @return a specification for updatedAt lower bound.
     */
    public static Specification<Shop> updatedAtAfterOrEqual(java.time.Instant minUpdatedAt) {
        return (root, query, cb) ->
                minUpdatedAt == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("updatedAt"), minUpdatedAt);
    }

    /**
     * Filters shops updated at or before the provided timestamp.
     *
     * @param maxUpdatedAt upper bound for updatedAt.
     * @return a specification for updatedAt upper bound.
     */
    public static Specification<Shop> updatedAtBeforeOrEqual(java.time.Instant maxUpdatedAt) {
        return (root, query, cb) ->
                maxUpdatedAt == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("updatedAt"), maxUpdatedAt);
    }

    /**
     * Filters shops that contain goods belonging to a specific category.
     * <p>Logic flow:</p>
     * <ul>
     * <li>Checks if categoryId is provided.</li>
     * <li>Performs a LEFT JOIN with the 'goods' association.</li>
     * <li>Enforces 'DISTINCT' on the query to prevent duplicate shop results when multiple products match.</li>
     * <li>Matches the provided UUID against the category ID within the goods.</li>
     * </ul>
     *
     * @param categoryId the unique identifier of the category.
     * @return a specification filtering shops by product category.
     */
    public static Specification<Shop> hasCategory(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }

            Join<Shop, Goods> goodsJoin = root.join("goods", JoinType.LEFT);

            if (query != null) {
                query.distinct(true);
            }

            return cb.equal(goodsJoin.get("category").get("id"), categoryId);
        };
    }


    /**
     * Filters shops where the name or description contains the given keyword (case-insensitive partial match).
     *
     * @param keyword the keyword to search for in name or description.
     * @return a specification for matching shops by name or description content.
     */
    public static Specification<Shop> filterByNameOrDescriptionContaining(String keyword) {
        return textSearch(keyword);
    }
}