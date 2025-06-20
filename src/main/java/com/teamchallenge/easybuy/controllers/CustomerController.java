package com.teamchallenge.easybuy.controllers;

import com.teamchallenge.easybuy.dto.AddressDto;
import com.teamchallenge.easybuy.dto.CustomerProfileDto;
import com.teamchallenge.easybuy.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCustomerProfile() {
        return ResponseEntity.ok(customerService.getCustomerProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateCustomerProfile(@Valid @RequestBody CustomerProfileDto customer, HttpServletRequest request) {
        System.out.println("1");
        return ResponseEntity.ok(customerService.updateCustomerProfile(customer, request));
    }

    @PutMapping("/address")
    public ResponseEntity<?> updateCustomerAddress(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(customerService.updateCustomerAddress(addressDto));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteCustomerProfile() {
        customerService.deleteCustomer();
        return ResponseEntity.noContent().build();
    }
}
