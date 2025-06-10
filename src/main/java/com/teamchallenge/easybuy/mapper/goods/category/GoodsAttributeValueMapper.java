package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.dto.goods.category.GoodsAttributeValueDTO;
import com.teamchallenge.easybuy.models.goods.category.GoodsAttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GoodsAttributeValueMapper {

    @Mapping(source = "goods.id", target = "goodsId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "attribute.id", target = "attributeId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GoodsAttributeValueDTO toDto(GoodsAttributeValue value);

    @Mapping(source = "goodsId", target = "goods.id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "attributeId", target = "attribute.id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GoodsAttributeValue toEntity(GoodsAttributeValueDTO dto);
}