package com.teamchallenge.easybuy.services;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.models.Address;
import com.teamchallenge.easybuy.repo.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public Address createAddress(AddressDto addressDto) {
        return addressRepository.save(Address.builder()
                .city(addressDto.getCity())
                .country(addressDto.getCountry())
                .street(addressDto.getStreet())
                .build());
    }

    public Address updateAddress(AddressDto addressDto) {
        Address address = addressRepository.findById(addressDto.getId())
                .orElseThrow(() -> new IllegalStateException("Address not found"));
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setStreet(addressDto.getStreet());
        return addressRepository.save(address);
    }

    public void deleteAddress(Address address) {
        addressRepository.delete(address);
    }
}
