package com.teamchallenge.easybuy.product.mapper;

import com.teamchallenge.easybuy.product.dto.GoodsImageDTO;
import com.teamchallenge.easybuy.product.entity.GoodsImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoodsImageMapper {

    GoodsImageMapper INSTANCE = Mappers.getMapper(GoodsImageMapper.class);

    @Mapping(source = "goods.id", target = "goodsId")
    GoodsImageDTO toDto(GoodsImage image);

    @Mapping(source = "goodsId", target = "goods.id")
    GoodsImage toEntity(GoodsImageDTO dto);
}