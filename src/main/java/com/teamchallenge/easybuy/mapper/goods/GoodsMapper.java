package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.models.goods.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface GoodsMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "shopId", source = "shopId")
    @Mapping(target = "goodsStatus", source = "goodsStatus", qualifiedByName = "enumToString")
    @Mapping(target = "discountStatus", source = "discountStatus", qualifiedByName = "enumToString")
    GoodsDTO toDto(Goods goods);

    @Mapping(target = "category", ignore = true) // Category will be set separately if needed
    @Mapping(target = "shopId", source = "shopId")
    @Mapping(target = "goodsStatus", source = "goodsStatus", qualifiedByName = "stringToGoodsStatus")
    @Mapping(target = "discountStatus", source = "discountStatus", qualifiedByName = "stringToDiscountStatus")
    Goods toEntity(GoodsDTO goodsDTO);

    @Named("enumToString")
    default String enumToString(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    @Named("stringToGoodsStatus")
    default Goods.GoodsStatus stringToGoodsStatus(String value) {
        return value != null ? Goods.GoodsStatus.valueOf(value.toUpperCase()) : null;
    }

    @Named("stringToDiscountStatus")
    default Goods.DiscountStatus stringToDiscountStatus(String value) {
        return value != null ? Goods.DiscountStatus.valueOf(value.toUpperCase()) : null;
    }
}