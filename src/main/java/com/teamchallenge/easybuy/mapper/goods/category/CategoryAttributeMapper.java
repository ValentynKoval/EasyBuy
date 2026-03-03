package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.domain.model.goods.category.AttributeType;
import com.teamchallenge.easybuy.dto.goods.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.domain.model.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryAttributeMapper {
    CategoryAttributeMapper INSTANCE = Mappers.getMapper(CategoryAttributeMapper.class);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapTypeToString")
    @Mapping(source = "category.id", target = "categoryId")
    CategoryAttributeDTO toDto(CategoryAttribute attribute);

    @Mapping(target = "type", expression = "java(com.teamchallenge.easybuy.domain.category.goods.model.AttributeType.valueOf(categoryAttributeDTO.getType()))")
    // Явно мапимо String на Enum
    @Mapping(source = "categoryId", target = "category.id")
    CategoryAttribute toEntity(CategoryAttributeDTO categoryAttributeDTO);

    @Named("mapTypeToString")
    default String mapTypeToString(AttributeType type) {
        return type != null ? type.toString() : null;
    }

    @Named("mapStringToType")
    default AttributeType mapStringToType(String type) {
        return type != null ? AttributeType.valueOf(type) : null;
    }
}