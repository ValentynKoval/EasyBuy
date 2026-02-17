package com.teamchallenge.easybuy.models.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shop_contact_info", indexes = {
        @Index(name = "idx_contact_info_shop_id", columnList = "shop_id"),
        @Index(name = "idx_contact_info_email", columnList = "contact_email"),
        @Index(name = "idx_contact_info_phone", columnList = "contact_phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Contains contact information of a shop.")
public class ShopContactInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "contact_info_id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the contact info record. Just for database. Read only",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID contactInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    @Schema(description = "The shop this contact info belongs to")
    private Shop shop;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "contact_email", length = 255)
    @Schema(description = "Primary contact email address", example = "contact@shop.com")
    private String contactEmail;

    @Pattern(regexp = "^[\\d\\s()+-]+$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "contact_phone", length = 20)
    @Schema(description = "Primary contact phone number", example = "+380931234567")
    private String contactPhone;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "support_email", length = 255)
    @Schema(description = "Support email address", example = "support@shop.com")
    private String supportEmail;

    @Pattern(regexp = "^[\\d\\s()+-]+$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "support_phone", length = 20)
    @Schema(description = "Support phone number", example = "+380931234568")
    private String supportPhone;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Website URL must not exceed 500 characters")
    @Column(name = "website_url", length = 500)
    @Schema(description = "URL of the shop's website", example = "https://shop.com")
    private String websiteUrl;

    @Pattern(regexp = "^(https?://)?[\\w./%-]+\\.(jpg|jpeg|png|webp|svg)$", message = "Invalid image URL")
    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    @Column(name = "logo_url", length = 500)
    @Schema(description = "URL of the shop's logo", example = "https://shop.com/logo.png")
    private String logoUrl;

    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    @Column(name = "contact_person_name", length = 100)
    @Schema(description = "Name of the primary contact person", example = "John Doe")
    private String contactPersonName;

    @Size(max = 100, message = "Contact person position must not exceed 100 characters")
    @Column(name = "contact_person_position", length = 100)
    @Schema(description = "Position of the primary contact person", example = "Sales Manager")
    private String contactPersonPosition;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(name = "business_address", length = 255)
    @Schema(description = "Business address", example = "123 Main Street, Kiev, Ukraine")
    private String businessAddress;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(name = "city", length = 100)
    @Schema(description = "City where the business is located", example = "Kiev")
    private String city;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(name = "country", length = 100)
    @Schema(description = "Country where the business is located", example = "Ukraine")
    private String country;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code", length = 20)
    @Schema(description = "Postal code", example = "01001")
    private String postalCode;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Social media URL must not exceed 500 characters")
    @Column(name = "facebook_url", length = 500)
    @Schema(description = "Facebook page URL", example = "https://facebook.com/shop")
    private String facebookUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Social media URL must not exceed 500 characters")
    @Column(name = "instagram_url", length = 500)
    @Schema(description = "Instagram profile URL", example = "https://instagram.com/shop")
    private String instagramUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Social media URL must not exceed 500 characters")
    @Column(name = "telegram_url", length = 500)
    @Schema(description = "Telegram channel/group URL", example = "https://t.me/shop")
    private String telegramUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Social media URL must not exceed 500 characters")
    @Column(name = "viber_url", length = 500)
    @Schema(description = "Viber group URL", example = "https://invite.viber.com/?g=shop")
    private String viberUrl;

    @Size(max = 1000, message = "Working hours must not exceed 1000 characters")
    @Column(name = "working_hours", columnDefinition = "TEXT")
    @Schema(description = "Working hours information", example = "Monday-Friday: 9:00-18:00, Saturday: 10:00-16:00")
    private String workingHours;

    @Size(max = 1000, message = "Additional info must not exceed 1000 characters")
    @Column(name = "additional_info", columnDefinition = "TEXT")
    @Schema(description = "Additional contact information", example = "24/7 customer support available")
    private String additionalInfo;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the contact information is verified", example = "false")
    private boolean isVerified = false;

    @Column(name = "verification_date")
    @Schema(description = "Date when contact information was verified", example = "2025-01-15T10:30:00Z")
    private Instant verificationDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the contact information is active", example = "true")
    private boolean isActive = true;

    @Column(name = "preferred_contact_method")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Preferred method of contact", example = "EMAIL")
    private ContactMethod preferredContactMethod;
    @Column(name = "created_at", updatable = false)
    @Schema(description = "When the contact info was created", example = "2025-01-15T10:30:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    // --- Timestamps ---
    @Column(name = "updated_at")
    @Schema(description = "When the contact info was last updated", example = "2025-01-15T10:30:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    /**
     * Check if contact information is complete
     */
    public boolean isComplete() {
        return contactEmail != null && !contactEmail.trim().isEmpty() &&
                contactPhone != null && !contactPhone.trim().isEmpty() &&
                businessAddress != null && !businessAddress.trim().isEmpty();
    }

    // --- Business methods ---

    /**
     * Get primary contact email
     */
    public String getPrimaryEmail() {
        return contactEmail != null && !contactEmail.trim().isEmpty() ? contactEmail : supportEmail;
    }

    /**
     * Get primary contact phone
     */
    public String getPrimaryPhone() {
        return contactPhone != null && !contactPhone.trim().isEmpty() ? contactPhone : supportPhone;
    }

    /**
     * Get full address
     */
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (businessAddress != null && !businessAddress.trim().isEmpty()) {
            address.append(businessAddress);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(postalCode);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(country);
        }
        return address.toString();
    }

    /**
     * Check if social media links are available
     */
    public boolean hasSocialMedia() {
        return (facebookUrl != null && !facebookUrl.trim().isEmpty()) ||
                (instagramUrl != null && !instagramUrl.trim().isEmpty()) ||
                (telegramUrl != null && !telegramUrl.trim().isEmpty()) ||
                (viberUrl != null && !viberUrl.trim().isEmpty());
    }

    /**
     * Get contact person full info
     */
    public String getContactPersonInfo() {
        if (contactPersonName == null || contactPersonName.trim().isEmpty()) {
            return null;
        }

        StringBuilder info = new StringBuilder(contactPersonName);
        if (contactPersonPosition != null && !contactPersonPosition.trim().isEmpty()) {
            info.append(" (").append(contactPersonPosition).append(")");
        }
        return info.toString();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // --- Lifecycle methods ---

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum ContactMethod {
        @Schema(description = "Contact via email")
        EMAIL,
        @Schema(description = "Contact via phone")
        PHONE,
        @Schema(description = "Contact via website")
        WEBSITE,
        @Schema(description = "Contact via social media")
        SOCIAL_MEDIA
    }
}