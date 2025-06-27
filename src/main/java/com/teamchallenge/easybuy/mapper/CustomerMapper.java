package com.teamchallenge.easybuy.mapper;

import com.teamchallenge.easybuy.dto.user.CustomerProfileDto;
import com.teamchallenge.easybuy.models.user.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerProfileDto toDto(Customer customer);
    Customer toEntity(CustomerProfileDto dto);
}
