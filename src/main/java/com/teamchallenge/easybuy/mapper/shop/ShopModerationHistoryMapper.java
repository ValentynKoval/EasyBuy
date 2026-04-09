package com.teamchallenge.easybuy.mapper.shop;

import com.teamchallenge.easybuy.domain.model.shop.ShopModerationHistory;
import com.teamchallenge.easybuy.dto.shop.shopmoderationhistory.ShopModerationHistoryDTO;
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
public interface ShopModerationHistoryMapper {

    @Mapping(source = "shop.shopId", target = "shopId")
    @Mapping(source = "moderator.id", target = "moderatorId")
    @Mapping(source = "reversedByUser.id", target = "reversedByUserId")
    @Mapping(source = "automatic", target = "automatic")
    @Mapping(source = "reversible", target = "reversible")
    ShopModerationHistoryDTO toDto(ShopModerationHistory entity);

    @Mapping(target = "moderationHistoryId", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "reversedByUser", ignore = true)
    @Mapping(target = "reversedAt", ignore = true)
    @Mapping(target = "impactScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "automatic", source = "automatic")
    @Mapping(target = "reversible", source = "reversible")
    ShopModerationHistory toEntity(ShopModerationHistoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "moderationHistoryId", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "reversedByUser", ignore = true)
    @Mapping(target = "reversedAt", ignore = true)
    @Mapping(target = "impactScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ShopModerationHistoryDTO dto, @MappingTarget ShopModerationHistory entity);
}

