package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.shop.ShopDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopMapper {

    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "moderatedByUser.id", target = "moderatedByUserId")
    ShopDTO toDto(Shop shop);

    // IMPORTANT:
    // Relationships are set manually in service layer
    Shop toEntity(ShopDTO shopDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ShopDTO dto, @MappingTarget Shop entity);
}