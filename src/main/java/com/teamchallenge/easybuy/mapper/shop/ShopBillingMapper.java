package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.ShopBillingInfo;
import com.teamchallenge.easybuy.dto.shop.shopbillinginfo.ShopBillingInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopBillingMapper {

    ShopBillingInfoDTO toDto(ShopBillingInfo entity);

    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stripeAccountId", ignore = true)
    ShopBillingInfo toEntity(ShopBillingInfoDTO dto);
}