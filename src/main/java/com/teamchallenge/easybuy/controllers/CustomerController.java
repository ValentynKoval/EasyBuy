package com.teamchallenge.easybuy.controllers;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.dto.CustomerDto;
import com.teamchallenge.easybuy.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(customerService.getUserInfo());
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomerDto> updateProfile(@Valid @RequestBody CustomerDto customerDto) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateProfile(customerDto));
    }

    @PutMapping("/address")
    public ResponseEntity<AddressDto> updateAddress(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateAddress(addressDto));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteProfile() {
        customerService.deleteProfile();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
