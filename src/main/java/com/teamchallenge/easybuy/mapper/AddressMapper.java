package com.teamchallenge.easybuy.mapper;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.models.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);
    Address toEntity(AddressDto dto);
}
