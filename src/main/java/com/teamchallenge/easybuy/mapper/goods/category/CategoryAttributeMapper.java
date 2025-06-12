package com.teamchallenge.easybuy.mapper.goods.category;

import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import com.teamchallenge.easybuy.dto.goods.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryAttributeMapper {
    CategoryAttributeMapper INSTANCE = Mappers.getMapper(CategoryAttributeMapper.class);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapTypeToString")
    @Mapping(source = "category.id", target = "categoryId")
    CategoryAttributeDTO toDto(CategoryAttribute attribute);

    @Mapping(target = "type", expression = "java(com.teamchallenge.easybuy.models.goods.category.AttributeType.valueOf(categoryAttributeDTO.getType()))") // Явно мапимо String на Enum
    @Mapping(source = "categoryId", target = "category.id")
    CategoryAttribute toEntity(CategoryAttributeDTO categoryAttributeDTO);

    @Named("mapTypeToString")
    default String mapTypeToString(com.teamchallenge.easybuy.models.goods.category.AttributeType type) {
        return type != null ? type.toString() : null;
    }

    @Named("mapStringToType")
    default com.teamchallenge.easybuy.models.goods.category.AttributeType mapStringToType(String type) {
        return type != null ? com.teamchallenge.easybuy.models.goods.category.AttributeType.valueOf(type) : null;
    }
}