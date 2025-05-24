package com.teamchallenge.easybuy.services.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.exceptions.GoodsNotFoundException;
import com.teamchallenge.easybuy.mapper.goods.GoodsMapper;
import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.repo.goods.GoodsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing goods, including CRUD operations, caching, and flexible search.
 */
@Service
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final GoodsMapper goodsMapper;

    /**
     * Retrieves all goods available in the system.
     *
     * @return List of GoodsDTO objects representing all goods.
     */
    @Operation(summary = "Get all goods", description = "Retrieves a list of all goods in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of goods",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Cacheable(value = "goods", key = "'allGoods'")
    public List<GoodsDTO> getAllGoods() {
        return goodsRepository.findAll()
                .stream()
                .map(goodsMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific good by its ID.
     *
     * @param id The UUID of the good to retrieve.
     * @return GoodsDTO object for the specified good.
     * @throws GoodsNotFoundException if the good is not found.
     */
    @Operation(summary = "Get good by ID", description = "Retrieves a specific good by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the good",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Good not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Cacheable(value = "goods", key = "#id")
    public GoodsDTO getGoodsById(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException("Good not found with id: " + id));
        return goodsMapper.toDto(goods);
    }

    /**
     * Creates a new good in the system.
     *
     * @param goodsDTO The GoodsDTO object containing the new good's details.
     * @return GoodsDTO object of the created good.
     * @throws IllegalArgumentException if the article number (art) already exists.
     */
    @Operation(summary = "Create a new good", description = "Creates a new good with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the good",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or article number already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @CacheEvict(value = {"goods", "goodsByCategory"}, allEntries = true)
    public GoodsDTO createGoods(GoodsDTO goodsDTO) {
        // Checking the uniqueness of art
        if (goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Article number already exists: " + goodsDTO.getArt());
        }
        Goods goods = goodsMapper.toEntity(goodsDTO);
        Goods savedGoods = goodsRepository.save(goods);
        return goodsMapper.toDto(savedGoods);
    }

    /**
     * Updates an existing good by its ID.
     *
     * @param id       The UUID of the good to update.
     * @param goodsDTO The GoodsDTO object with updated details.
     * @return GoodsDTO object of the updated good.
     * @throws GoodsNotFoundException   if the good is not found.
     * @throws IllegalArgumentException if the article number (art) already exists for another good.
     */
    @Operation(summary = "Update a good", description = "Updates an existing good with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the good",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Good not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or article number already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @CacheEvict(value = {"goods", "goodsByCategory"}, key = "#id")
    public GoodsDTO updateGoods(UUID id, GoodsDTO goodsDTO) {
        Goods existingGoods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException("Good not found with id: " + id));
        // Checking the uniqueness of art, if art changed
        if (!existingGoods.getArt().equals(goodsDTO.getArt()) && goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Article number already exists: " + goodsDTO.getArt());
        }
        Goods updatedGoods = goodsMapper.toEntity(goodsDTO);
        updatedGoods.setId(id); // Preserve the ID
        updatedGoods.setCategory(existingGoods.getCategory()); // Preserve category if not provided in DTO
        updatedGoods.setShopId(existingGoods.getShopId()); // Preserve shopId if not provided
        Goods savedGoods = goodsRepository.save(updatedGoods);
        return goodsMapper.toDto(savedGoods);
    }

    /**
     * Deletes a good by its ID.
     *
     * @param id The UUID of the good to delete.
     * @throws GoodsNotFoundException if the good is not found.
     */
    @Operation(summary = "Delete a good", description = "Deletes a specific good by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the good"),
            @ApiResponse(responseCode = "404", description = "Good not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @CacheEvict(value = {"goods", "goodsByCategory"}, key = "#id")
    public void deleteGoods(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));
       
        // Call CrudRepository.delete
        ((org.springframework.data.repository.CrudRepository<Goods, UUID>) goodsRepository).delete(goods);
    }

    /**
     * Searches for goods based on multiple fields and their combinations.
     *
     * @param searchParams Map of field names and their values to search for (e.g., name, art, price).
     * @return List of GoodsDTO objects matching the search criteria.
     */
    @Operation(summary = "Search goods", description = "Searches for goods based on any combination of fields " +
            "such as art, name, price, etc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of goods",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<GoodsDTO> searchGoods(Map<String, String> searchParams) {
        Specification<Goods> spec = buildSpecification(searchParams);
        return goodsRepository.findAll(spec)
                .stream()
                .map(goodsMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Builds a dynamic Specification for searching goods based on provided parameters.
     *
     * @param searchParams Map of field names and their values.
     * @return Specification object for JPA query.
     */
    private Specification<Goods> buildSpecification(Map<String, String> searchParams) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String> entry : searchParams.entrySet()) {
                String key = entry.getKey().toLowerCase();
                String value = entry.getValue();
                switch (key) {
                    case "art":
                        predicates.add(criteriaBuilder.like(root.get("art"), "%" + value + "%"));
                        break;
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                        break;
                    case "description":
                        predicates.add(criteriaBuilder.like(root.get("description"), "%" + value + "%"));
                        break;
                    case "price":
                        try {
                            BigDecimal price = new BigDecimal(value);
                            predicates.add(criteriaBuilder.equal(root.get("price"), price));
                        } catch (NumberFormatException e) {
                            // Ignore invalid price format
                        }
                        break;
                    case "stock":
                        try {
                            Integer stock = Integer.parseInt(value);
                            predicates.add(criteriaBuilder.equal(root.get("stock"), stock));
                        } catch (NumberFormatException e) {
                            // Ignore invalid stock format
                        }
                        break;
                    case "shopid":
                        try {
                            UUID shopId = UUID.fromString(value);
                            predicates.add(criteriaBuilder.equal(root.get("shopId"), shopId));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid UUID format
                        }
                        break;
                    case "categoryid":
                        try {
                            UUID categoryId = UUID.fromString(value);
                            predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid UUID format
                        }
                        break;
                    case "goodsstatus":
                        try {
                            Goods.GoodsStatus status = Goods.GoodsStatus.valueOf(value.toUpperCase());
                            predicates.add(criteriaBuilder.equal(root.get("goodsStatus"), status));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid enum value
                        }
                        break;
                    case "discountstatus":
                        try {
                            Goods.DiscountStatus discountStatus = Goods.DiscountStatus.valueOf(value.toUpperCase());
                            predicates.add(criteriaBuilder.equal(root.get("discountStatus"), discountStatus));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid enum value
                        }
                        break;
                    case "discountvalue":
                        try {
                            BigDecimal discountValue = new BigDecimal(value);
                            predicates.add(criteriaBuilder.equal(root.get("discountValue"), discountValue));
                        } catch (NumberFormatException e) {
                            // Ignore invalid discount value format
                        }
                        break;
                }
            }
            // If the predicate list is empty, return conjunction (always true)
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

}