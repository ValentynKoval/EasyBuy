package com.teamchallenge.easybuy.models.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "shop_contact_info", indexes = {
        @Index(name = "idx_contact_info_shop_id", columnList = "shop_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Contains contact information of a shop.")
public class ShopContactInfo {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the contact info record. Just for database. Read only",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    @Schema(description = "The shop this contact info belongs to")
    private Shop shop;

    @Email
    @Column(name = "contact_email")
    @Schema(description = "Contact email address", example = "contact@shop.com")
    private String contactEmail;

    @Pattern(regexp = "^[\\d\\s()+-]+$", message = "Invalid phone number format")
    @Column(name = "contact_phone")
    @Schema(description = "Contact phone number", example = "+380931234567")
    private String contactPhone;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Column(name = "website_url")
    @Schema(description = "URL of the shop's website", example = "https://shop.com")
    private String websiteUrl;

    @Pattern(regexp = "^(https?://)?[\\w./%-]+\\.(jpg|jpeg|png|webp)$", message = "Invalid image URL")
    @Column(name = "logo_url")
    @Schema(description = "URL of the shop's logo", example = "https://shop.com/logo.png")
    private String logoUrl;
}
