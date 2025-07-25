package com.teamchallenge.easybuy.services.user;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.dto.user.CustomerProfileDto;
import com.teamchallenge.easybuy.dto.user.CustomerProfileResponseDto;
import com.teamchallenge.easybuy.mapper.AddressMapper;
import com.teamchallenge.easybuy.mapper.CustomerMapper;
import com.teamchallenge.easybuy.models.Address;
import com.teamchallenge.easybuy.models.user.Customer;
import com.teamchallenge.easybuy.repo.user.CustomerRepository;
import com.teamchallenge.easybuy.services.goods.image.CloudinaryImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final EmailConfirmationService emailConfirmationService;
    private final TokenService tokenService;
    private final PasswordResetService passwordResetService;
    private final CloudinaryImageService cloudinaryImageService;
    private final PhoneValidationService phoneValidationService;

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
        customer.setPhoneNumber(phoneValidationService.formatToE164(customerProfile.getPhoneNumber()));
        if (!customer.getEmail().equals(customerProfile.getEmail())) {
            customer.setEmail(customerProfile.getEmail());
            customer.setEmailVerified(false);
            emailConfirmationService.sendConfirmationEmail(customer);
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

    @Transactional
    public void deleteCustomer() throws IOException {
        Customer customer = getCustomer();
        String avatarUrl = customer.getAvatarUrl();
        String publicId = cloudinaryImageService.extractPublicIdFromUrl(avatarUrl);
        if (publicId != null) {
            cloudinaryImageService.deleteImage(publicId);
        }
        tokenService.deleteAllTokensForUser(customer);
        emailConfirmationService.deleteAllByUser(customer);
        passwordResetService.deleteAllByUser(customer);
        customerRepository.delete(customer);
    }

    private Customer getCustomer() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return customerRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + username));
    }

}
