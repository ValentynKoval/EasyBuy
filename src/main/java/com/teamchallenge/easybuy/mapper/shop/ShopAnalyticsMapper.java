package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.ShopAnalytics;
import com.teamchallenge.easybuy.dto.shop.shopanalytics.ShopAnalyticsDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopAnalyticsMapper {

    ShopAnalyticsDTO toDto(ShopAnalytics entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "conversionRate", ignore = true)
    @Mapping(target = "cancellationRate", ignore = true)
    @Mapping(target = "returnRate", ignore = true)
    @Mapping(target = "healthScore", ignore = true)
    @Mapping(target = "inactiveDays", ignore = true)
    @Mapping(target = "deadShop", ignore = true)
    @Mapping(target = "lastCalculatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShopAnalytics toEntity(ShopAnalyticsDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "conversionRate", ignore = true)
    @Mapping(target = "cancellationRate", ignore = true)
    @Mapping(target = "returnRate", ignore = true)
    @Mapping(target = "healthScore", ignore = true)
    @Mapping(target = "inactiveDays", ignore = true)
    @Mapping(target = "deadShop", ignore = true)
    @Mapping(target = "lastCalculatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ShopAnalyticsDTO dto, @MappingTarget ShopAnalytics entity);
}

