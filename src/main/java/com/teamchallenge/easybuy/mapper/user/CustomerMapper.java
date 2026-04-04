package com.teamchallenge.easybuy.mapper.user;

import com.teamchallenge.easybuy.dto.user.CustomerProfileDto;
import com.teamchallenge.easybuy.domain.model.user.Customer;
import org.mapstruct.Builder; // Не забудь импортировать Builder из MapStruct!
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerMapper {


    CustomerProfileDto toDto(Customer customer);

    Customer toEntity(CustomerProfileDto dto);
}