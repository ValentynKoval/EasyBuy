package com.teamchallenge.easybuy.repository.shop;

import com.teamchallenge.easybuy.domain.model.goods.Goods;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
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