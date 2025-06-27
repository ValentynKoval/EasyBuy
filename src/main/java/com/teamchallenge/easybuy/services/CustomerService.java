package com.teamchallenge.easybuy.services;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.dto.CustomerProfileDto;
import com.teamchallenge.easybuy.dto.CustomerProfileResponseDto;
import com.teamchallenge.easybuy.mapper.AddressMapper;
import com.teamchallenge.easybuy.mapper.CustomerMapper;
import com.teamchallenge.easybuy.models.Address;
import com.teamchallenge.easybuy.models.Customer;
import com.teamchallenge.easybuy.repo.CustomerRepository;
import com.teamchallenge.easybuy.services.user.EmailConfirmationService;
import com.teamchallenge.easybuy.services.user.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final EmailConfirmationService emailConfirmationService;
    private final TokenService tokenService;

    public CustomerProfileResponseDto getCustomerProfile() {
        Customer customer = getCustomer();
        return new CustomerProfileResponseDto(
                customerMapper.toDto(customer),
                addressMapper.toDto(customer.getAddress()));
    }

    public CustomerProfileDto updateCustomerProfile(CustomerProfileDto customerProfile, HttpServletRequest request) {
        Customer customer = getCustomer();
        customer.setName(customerProfile.getName());
        customer.setBirthday(customerProfile.getBirthday());
        customer.setPhoneNumber(customerProfile.getPhoneNumber());
        if (!customer.getEmail().equals(customerProfile.getEmail())) {
            customer.setEmail(customerProfile.getEmail());
            customer.setEmailVerified(false);
            String baseUrl = ServletUriComponentsBuilder
                    .fromRequestUri(request)
                    .replacePath(null)
                    .build()
                    .toUriString();
            emailConfirmationService.sendConfirmationEmail(customer, baseUrl);
        }

        return customerMapper.toDto(customerRepository.save(customer));
    }

    public AddressDto updateCustomerAddress(AddressDto addressDto) {
        Customer customer = getCustomer();
        Address address = customer.getAddress();
        if  (address == null) {
            address = new Address();
            customer.setAddress(address);
        }
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setStreet(addressDto.getStreet());
        Customer newCustomer = customerRepository.save(customer);
        return addressMapper.toDto(newCustomer.getAddress());
    }

    public void deleteCustomer() {
        Customer customer = getCustomer();
        tokenService.deleteAllTokensForUser(customer);
        emailConfirmationService.deleteAllByUser(customer);
        customerRepository.delete(customer);
    }

    private Customer getCustomer() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return customerRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Customer not found: " + username));
    }

}
