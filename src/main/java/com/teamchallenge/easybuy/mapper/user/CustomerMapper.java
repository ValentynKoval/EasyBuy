package com.teamchallenge.easybuy.mapper.user;

import com.teamchallenge.easybuy.dto.user.CustomerProfileDto;
import com.teamchallenge.easybuy.domain.model.user.Customer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerMapper {


    CustomerProfileDto toDto(Customer customer);

    Customer toEntity(CustomerProfileDto dto);
}