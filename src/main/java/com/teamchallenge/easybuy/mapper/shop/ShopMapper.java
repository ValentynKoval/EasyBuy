package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopMapper {

    @Mapping(source = "seller.id", target = "sellerId")

    // TODO: When moderation logic is implemented,
    // map moderatedByUser.id to moderatedByUserId for response DTO
    @Mapping(source = "moderatedByUser.id", target = "moderatedByUserId")

    ShopDTO toDto(Shop shop);

    @Mapping(source = "sellerId", target = "seller.id")

        // IMPORTANT:
        // Do NOT map moderatedByUserId back to entity.
        // This field must be set only by moderation service logic.
    Shop toEntity(ShopDTO shopDTO);
}