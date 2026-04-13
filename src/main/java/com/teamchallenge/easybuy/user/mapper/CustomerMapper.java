package com.teamchallenge.easybuy.user.mapper;

import com.teamchallenge.easybuy.user.dto.CustomerProfileDto;
import com.teamchallenge.easybuy.user.entity.Customer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerMapper {


    CustomerProfileDto toDto(Customer customer);

    Customer toEntity(CustomerProfileDto dto);
}