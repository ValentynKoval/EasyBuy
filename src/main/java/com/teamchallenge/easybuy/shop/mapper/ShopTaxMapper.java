package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.entity.ShopTaxInfo;
import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopTaxMapper {

    ShopTaxInfoDTO toDto(ShopTaxInfo entity);

    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "id", ignore = true)
    ShopTaxInfo toEntity(ShopTaxInfoDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ShopTaxInfoDTO dto, @MappingTarget ShopTaxInfo entity);
}