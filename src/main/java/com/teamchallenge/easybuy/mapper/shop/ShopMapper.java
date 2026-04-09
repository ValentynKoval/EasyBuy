package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                ShopContactInfoMapper.class,
                ShopBillingMapper.class, // Подключаем маппер биллинга
                ShopTaxMapper.class      // Подключаем маппер налогов
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopMapper {

    // ===== ENTITY → DTO =====

    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "moderatedByUser.id", target = "moderatedByUserId")
    @Mapping(source = "shopContactInfo", target = "shopContactInfo")
    @Mapping(source = "shopBillingInfo", target = "shopBillingInfo") // Маппинг Stripe данных
    @Mapping(source = "shopTaxInfo", target = "shopTaxInfo")         // Маппинг налоговых данных
    ShopDTO toDto(Shop shop);


    // ===== DTO → ENTITY =====

    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "moderatedByUser", ignore = true)
    @Mapping(target = "shopContactInfo", ignore = true)
    @Mapping(target = "shopBillingInfo", ignore = true) // Управляется через StripeService
    @Mapping(target = "shopTaxInfo", ignore = true)     // Управляется через отдельный сервис
    Shop toEntity(ShopDTO dto);


    // ===== PARTIAL UPDATE =====

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "moderatedByUser", ignore = true)
    @Mapping(target = "shopContactInfo", ignore = true)
    @Mapping(target = "shopBillingInfo", ignore = true)
    @Mapping(target = "shopTaxInfo", ignore = true)
    void updateEntityFromDto(ShopDTO dto, @MappingTarget Shop entity);


    // ===== POST PROCESSING =====

    @AfterMapping
    default void afterMappingToEntity(ShopDTO dto, @MappingTarget Shop entity) {
        // Логика формирования Slug
        if (entity.getSlug() == null && entity.getShopName() != null) {
            entity.setSlug(
                    entity.getShopName()
                            .trim()
                            .toLowerCase()
                            .replaceAll("[^a-z0-9-]", "-")
                            .replaceAll("-+", "-")
                            .replaceAll("^-|-$", "")
            );
        }

        if (entity.getShopStatus() == null) {
            entity.setShopStatus(Shop.ShopStatus.PENDING);
        }

        if (entity.getShopType() == null) {
            entity.setShopType(Shop.ShopType.RETAILER);
        }
    }
}