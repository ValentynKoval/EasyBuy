package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.dto.goods.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryAttributeMapper {

    @Mapping(source = "type", target = "type", qualifiedByName = "mapTypeToString")
    @Mapping(source = "category.id", target = "categoryId")
    CategoryAttributeDTO toDto(CategoryAttribute attribute);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapStringToType")
    @Mapping(source = "categoryId", target = "category.id")
    CategoryAttribute toEntity(CategoryAttributeDTO dto);

    @Named("mapTypeToString")
    default String mapTypeToString(com.teamchallenge.easybuy.models.goods.category.AttributeType type) {
        return type != null ? type.toString() : null;
    }

    @Named("mapStringToType")
    default com.teamchallenge.easybuy.models.goods.category.AttributeType mapStringToType(String type) {
        return type != null ? com.teamchallenge.easybuy.models.goods.category.AttributeType.valueOf(type) : null;
    }
}