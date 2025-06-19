package com.teamchallenge.easybuy.services;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.dto.CustomerDto;
import com.teamchallenge.easybuy.mapper.AddressMapper;
import com.teamchallenge.easybuy.mapper.UserMapper;
import com.teamchallenge.easybuy.models.Address;
import com.teamchallenge.easybuy.models.User;
import com.teamchallenge.easybuy.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressService addressService;
    private final AddressMapper addressMapper;

    public CustomerDto getUserInfo() {
        return userMapper.toDto(getUser());
    }

    public CustomerDto updateProfile(CustomerDto customerDto) {
        User user = getUser();
        user.setEmail(customerDto.getEmail());
        user.setPhoneNumber(customerDto.getPhoneNumber());
        user.setName(customerDto.getName());
        user.setBirthday(customerDto.getBirthday());

        return userMapper.toDto(userRepository.save(user));
    }

    public AddressDto updateAddress(AddressDto addressDto) {
        Address address;
        if (addressDto != null) {
            User  user = getUser();
            Address oldAddress = user.getAddress();
            if (oldAddress == null) {
                oldAddress = new Address();
            }
            oldAddress.setCity(addressDto.getCity());
            oldAddress.setCountry(addressDto.getCountry());
            oldAddress.setStreet(addressDto.getStreet());
            address = addressService.createAddress(oldAddress);
            user.setAddress(address);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("Address not found");
        }

        return addressMapper.toDto(address);
    }

    public void deleteProfile() {
        User user = getUser();
        if (user.getAddress() != null) {
            addressService.deleteAddress(user.getAddress());
            user.setAddress(null);
        }
        userRepository.delete(user);
    }

    private User getUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }
}
