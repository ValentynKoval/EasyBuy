package com.teamchallenge.easybuy.dto.user;

import com.teamchallenge.easybuy.validation.ValidPhone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @Schema(description = "Store name", example = "MyStore")
    private String storeName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    @Schema(description = "User email", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @ValidPhone
    @Schema(description = "User's phone number in the format +380...", example = "+380931234567", required = true)
    private String phoneNumber;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 50, message = "Password must contain 6 to 50 characters")
    @Schema(description = "User password", example = "MySecurePassword1", required = true)
    private String password;

    @NotBlank(message = "Password confirmation cannot be empty")
    @Schema(description = "Password confirmation", example = "MySecurePassword1", required = true)
    private String confirmPassword;

    @NotBlank(message = "Role cannot be empty.")
    @Schema(description = "User role (e.g. SELLER, CUSTOMER or ADMIN)", example = "CUSTOMER", required = true)
    private String role;
}
