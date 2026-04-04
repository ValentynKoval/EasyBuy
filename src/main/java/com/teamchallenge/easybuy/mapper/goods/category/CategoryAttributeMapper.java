package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.domain.model.goods.category.AttributeType;
import com.teamchallenge.easybuy.dto.goods.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.domain.model.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryAttributeMapper {

    CategoryAttributeMapper INSTANCE = Mappers.getMapper(CategoryAttributeMapper.class);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapTypeToString")
    @Mapping(source = "category.id", target = "categoryId")
    CategoryAttributeDTO toDto(CategoryAttribute attribute);

    // ✅ ВОТ ГЛАВНОЕ ИСПРАВЛЕНИЕ
    @Mapping(source = "type", target = "type", qualifiedByName = "mapStringToType")
    @Mapping(source = "categoryId", target = "category.id")
    CategoryAttribute toEntity(CategoryAttributeDTO categoryAttributeDTO);

    @Named("mapTypeToString")
    default String mapTypeToString(AttributeType type) {
        return type != null ? type.name() : null;
    }

    @Named("mapStringToType")
    default AttributeType mapStringToType(String type) {
        try {
            return type != null ? AttributeType.valueOf(type) : null;
        } catch (Exception e) {
            return AttributeType.STRING; // fallback
        }
    }
}