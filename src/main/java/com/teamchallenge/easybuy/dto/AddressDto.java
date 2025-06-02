package com.teamchallenge.easybuy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AddressDto {
    private UUID id;
    private String country;
    private String city;
    private String street;
}
