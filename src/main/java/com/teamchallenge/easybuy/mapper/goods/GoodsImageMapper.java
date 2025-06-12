package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsImageDTO;
import com.teamchallenge.easybuy.models.goods.GoodsImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoodsImageMapper {

    GoodsImageMapper INSTANCE = Mappers.getMapper(GoodsImageMapper.class);

    @Mapping(source = "goods.id", target = "goodsId")
    GoodsImageDTO toDto(GoodsImage image);

    @Mapping(source = "goodsId", target = "goods.id")
    GoodsImage toEntity(GoodsImageDTO dto);
}