package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.ShopSeoSettings;
import com.teamchallenge.easybuy.dto.shop.ShopSeoSettingsDTO;
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
public interface ShopSeoSettingsMapper {

    ShopSeoSettingsDTO toDto(ShopSeoSettings entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "seoScore", ignore = true)
    @Mapping(target = "lastSeoAudit", ignore = true)
    @Mapping(target = "seoOptimized", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShopSeoSettings toEntity(ShopSeoSettingsDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "seoScore", ignore = true)
    @Mapping(target = "lastSeoAudit", ignore = true)
    @Mapping(target = "seoOptimized", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ShopSeoSettingsDTO dto, @MappingTarget ShopSeoSettings entity);
}

