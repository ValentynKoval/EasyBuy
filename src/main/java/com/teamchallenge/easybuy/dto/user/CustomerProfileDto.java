package com.teamchallenge.easybuy.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerProfileDto {
    @Schema(description = "Customer name", example = "Ivanov Ivan")
    private String name;

    @Past(message = "The date of birth must be in the past")
    @NotNull(message = "Date of birth is required")
    @Schema(description = "Customer's birthday", example = "2000-10-31")
    private LocalDate birthday;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Incorrect phone number format")
    @Schema(description = "User's phone number in the format +380...", example = "+380931234567", required = true)
    private String phoneNumber;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    @Schema(description = "User email", example = "user@example.com", required = true)
    private String email;
}
