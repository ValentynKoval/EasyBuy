package com.teamchallenge.easybuy.user.mapper;

import com.teamchallenge.easybuy.user.dto.AddressDto;
import com.teamchallenge.easybuy.user.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);

    Address toEntity(AddressDto dto);
}
