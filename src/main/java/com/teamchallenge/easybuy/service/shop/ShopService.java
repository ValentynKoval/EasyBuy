package com.teamchallenge.easybuy.service.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import com.teamchallenge.easybuy.dto.shop.ShopSearchParams;
import com.teamchallenge.easybuy.exception.DuplicateResourceException;
import com.teamchallenge.easybuy.exception.ResourceNotFoundException;
import com.teamchallenge.easybuy.mapper.shop.ShopMapper;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.shop.ShopSpecifications;
import com.teamchallenge.easybuy.repository.user.UserRepository;
import com.teamchallenge.easybuy.repository.user.seller.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer responsible for managing {@link Shop} entities.
 *
 * <p>This service provides:
 * <ul>
 *     <li>CRUD operations for shops</li>
 *     <li>Search and filtering using JPA Specifications</li>
 *     <li>Validation of unique shop fields</li>
 *     <li>Mapping between entity and DTO</li>
 *     <li>Managing relationships with Seller and Moderator users</li>
 * </ul>
 *
 * <p>The service operates within transactional boundaries and ensures
 * data integrity during create and update operations.</p>
 */
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    /**
     * Returns paginated list of all shops.
     */
    @Transactional(readOnly = true)
    public Page<ShopDTO> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable)
                .map(shopMapper::toDto);
    }

    /**
     * Returns shop by its identifier.
     *
     * @param id shop identifier
     * @return shop DTO
     * @throws ResourceNotFoundException if shop does not exist
     */
    @Transactional(readOnly = true)
    public ShopDTO getShopById(UUID id) {
        return shopRepository.findById(id)
                .map(shopMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));
    }

    /**
     * Creates a new shop.
     *
     * @param shopDTO shop data
     * @return created shop DTO
     * @throws DuplicateResourceException if shop name already exists
     */
    @Transactional
    public ShopDTO createShop(ShopDTO shopDTO) {

        validateShopNameUnique(shopDTO.getShopName());

        Shop shop = shopMapper.toEntity(shopDTO);

        setShopRelations(shop, shopDTO);

        return shopMapper.toDto(shopRepository.save(shop));
    }

    /**
     * Updates an existing shop.
     *
     * <p>Only fields provided in DTO will be updated.</p>
     *
     * @param id shop identifier
     * @param shopDTO updated shop data
     * @return updated shop DTO
     */
    @Transactional
    public ShopDTO updateShop(UUID id, ShopDTO shopDTO) {

        Shop existingShop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + id));

        if (shopDTO.getShopName() != null &&
                !shopDTO.getShopName().equals(existingShop.getShopName())) {

            validateShopNameUnique(shopDTO.getShopName());
        }

        shopMapper.updateEntityFromDto(shopDTO, existingShop);

        setShopRelations(existingShop, shopDTO);

        return shopMapper.toDto(shopRepository.save(existingShop));
    }

    /**
     * Searches shops using dynamic filtering parameters.
     *
     * @param params filtering parameters
     * @param pageable pagination configuration
     * @return page of filtered shops
     */
    @Transactional(readOnly = true)
    public Page<ShopDTO> searchShops(ShopSearchParams params, Pageable pageable) {

        Specification<Shop> spec = Specification.where(null);

        if (params.getShopName() != null && !params.getShopName().isBlank()) {
            spec = spec.and(ShopSpecifications.likeName(params.getShopName()));
        }

        if (params.getShopStatus() != null) {
            spec = spec.and(ShopSpecifications.hasStatus(params.getShopStatus()));
        }

        if (params.getIsFeatured() != null) {
            spec = spec.and(ShopSpecifications.isFeatured(params.getIsFeatured()));
        }

        if (params.getKeyword() != null && !params.getKeyword().isBlank()) {
            spec = spec.and(ShopSpecifications.filterByNameOrDescriptionContaining(params.getKeyword()));
        }

        return shopRepository.findAll(spec, pageable)
                .map(shopMapper::toDto);
    }

    /**
     * Validates that shop name is unique.
     */
    private void validateShopNameUnique(String shopName) {
        if (shopRepository.existsByShopName(shopName)) {
            throw new DuplicateResourceException("Shop name already exists: " + shopName);
        }
    }

    /**
     * Sets relational fields for the shop entity.
     *
     * <p>This method attaches seller and moderator entities if their IDs
     * are provided in the DTO.</p>
     */
    private void setShopRelations(Shop shop, ShopDTO dto) {

        if (dto.getSellerId() != null) {
            shop.setSeller(
                    sellerRepository.findById(dto.getSellerId())
                            .orElseThrow(() -> new ResourceNotFoundException("Seller not found"))
            );
        }

        if (dto.getModeratedByUserId() != null) {
            shop.setModeratedByUser(
                    userRepository.findById(dto.getModeratedByUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("Moderator not found"))
            );
        }
    }
}