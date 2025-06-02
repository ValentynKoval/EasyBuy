package com.teamchallenge.easybuy.mapper;

import com.teamchallenge.easybuy.dto.CustomerDto;
import com.teamchallenge.easybuy.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {
    CustomerDto toDto(User user);
    User toEntity(CustomerDto dto);
}
