package com.teamchallenge.easybuy.services.users;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.dto.user.CustomerProfileDto;
import com.teamchallenge.easybuy.dto.user.CustomerProfileResponseDto;
import com.teamchallenge.easybuy.mapper.AddressMapper;
import com.teamchallenge.easybuy.mapper.CustomerMapper;
import com.teamchallenge.easybuy.models.Address;
import com.teamchallenge.easybuy.models.user.Customer;
import com.teamchallenge.easybuy.models.user.Role;
import com.teamchallenge.easybuy.repo.user.CustomerRepository;
import com.teamchallenge.easybuy.services.goods.image.CloudinaryImageService;
import com.teamchallenge.easybuy.services.user.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private EmailConfirmationService emailConfirmationService;
    @Mock
    private TokenService tokenService;
    @Mock
    private PasswordResetService passwordResetService;
    @Mock
    private CloudinaryImageService cloudinaryImageService;
    @Mock
    private PhoneValidationService phoneValidationService;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .email("test@example.com")
                .role(Role.CUSTOMER)
                .build();
        customer.setEmailVerified(true);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(customer.getEmail());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
    }

    @Test
    @DisplayName("getCustomerProfile should return profile and address from mappers")
    void getCustomerProfile_ShouldReturnMappedDtos() {
        mockSecurityContext();
        CustomerProfileDto profileDto = new CustomerProfileDto();
        AddressDto addressDto = new AddressDto("Country", "City", "Street");
        when(customerMapper.toDto(customer)).thenReturn(profileDto);
        when(addressMapper.toDto(customer.getAddress())).thenReturn(addressDto);

        CustomerProfileResponseDto responseDto = customerService.getCustomerProfile();

        assertSame(profileDto, responseDto.getProfile());
        assertSame(addressDto, responseDto.getAddress());
    }

    @Test
    @DisplayName("updateCustomerProfile should update fields and send confirmation when email changed")
    void updateCustomerProfile_ShouldUpdateFieldsAndSendConfirmation_WhenEmailChanged() {
        mockSecurityContext();
        customer.setEmail("old@example.com");
        customer.setName("Old Name");
        customer.setPhoneNumber("old");
        customer.setBirthday(LocalDate.of(1990, 1, 1));

        CustomerProfileDto requestDto = new CustomerProfileDto();

        requestDto.setName("New Name");
        requestDto.setBirthday(LocalDate.of(1995, 5, 5));
        requestDto.setPhoneNumber("12345");
        requestDto.setEmail("new@example.com");

        when(phoneValidationService.formatToE164("12345")).thenReturn("+12345");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        CustomerProfileDto mappedDto = new CustomerProfileDto();
        when(customerMapper.toDto(customer)).thenReturn(mappedDto);

        CustomerProfileDto result = customerService.updateCustomerProfile(requestDto);

        assertEquals("New Name", customer.getName());
        assertEquals(LocalDate.of(1995, 5, 5), customer.getBirthday());
        assertEquals("+12345", customer.getPhoneNumber());
        assertEquals("new@example.com", customer.getEmail());
        assertFalse(customer.isEmailVerified());
        verify(emailConfirmationService).sendConfirmationEmail(customer);
        verify(customerRepository).save(customer);
        assertSame(mappedDto, result);
    }

    @Test
    @DisplayName("updateCustomerProfile should not send confirmation when email unchanged")
    void updateCustomerProfile_ShouldNotSendConfirmation_WhenEmailUnChanged() {
        mockSecurityContext();
        customer.setEmail("same@example.com");
        customer.setEmailVerified(true);
        CustomerProfileDto requestDto = new CustomerProfileDto();
        requestDto.setName("Name");
        requestDto.setBirthday(LocalDate.of(1990, 1, 1));
        requestDto.setPhoneNumber("12345");
        requestDto.setEmail("same@example.com");
        when(phoneValidationService.formatToE164("12345")).thenReturn("+12345");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        CustomerProfileDto mapped = new CustomerProfileDto();
        when(customerMapper.toDto(customer)).thenReturn(mapped);

        customerService.updateCustomerProfile(requestDto);

        verify(emailConfirmationService, never()).sendConfirmationEmail(customer);
        assertTrue(customer.isEmailVerified());
    }

    @Test
    @DisplayName("updateCustomerAddress should create and update address")
    void updateCustomerAddress_ShouldCreateAndUpdateAddress() {
        mockSecurityContext();
        AddressDto requestDto = new AddressDto("Country", "City", "Street");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        AddressDto mapped = new AddressDto("Country", "City", "Street");
        when(addressMapper.toDto(any(Address.class))).thenReturn(mapped);

        AddressDto result = customerService.updateCustomerAddress(requestDto);

        assertNotNull(customer.getAddress());
        assertEquals("Country", customer.getAddress().getCountry());
        assertEquals("City", customer.getAddress().getCity());
        assertEquals("Street", customer.getAddress().getStreet());
        verify(customerRepository).save(customer);
        assertSame(mapped, result);
    }

    @Test
    @DisplayName("deleteCustomer should remove avatar and related tokens")
    void deleteCustomer_ShouldRemoveAvatarAndRelatedTokens() throws IOException {
        mockSecurityContext();
        customer.setAvatarUrl("http://example.com/image.jpg");
        when(cloudinaryImageService.extractPublicIdFromUrl(customer.getAvatarUrl())).thenReturn("publicId");

        customerService.deleteCustomer();

        verify(cloudinaryImageService).deleteImage("publicId");
        verify(tokenService).deleteAllTokensForUser(customer);
        verify(emailConfirmationService).deleteAllByUser(customer);
        verify(passwordResetService).deleteAllByUser(customer);
        verify(customerRepository).delete(customer);
    }
}
