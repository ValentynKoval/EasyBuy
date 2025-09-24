package com.teamchallenge.easybuy.models.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.Convert;
import com.teamchallenge.easybuy.persistence.converter.CryptoStringConverter;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shop_billing_info", indexes = {
        @Index(name = "idx_billing_info_shop_id", columnList = "shop_id"),
        @Index(name = "idx_billing_info_paypal_client_id", columnList = "paypal_client_id"),
        @Index(name = "idx_billing_info_status", columnList = "billing_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Contains billing and payment gateway information for a shop integration with PayPal.")
public class ShopBillingInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "billing_info_id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the billing info record. Read only",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID billingInfoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    @Schema(description = "The shop this billing info belongs to")
    private Shop shop;

    // PayPal Configuration
    @Convert(converter = CryptoStringConverter.class)
    @NotBlank(message = "PayPal client ID is required")
    @Size(max = 255, message = "Client ID must not exceed 255 characters")
    @Column(name = "paypal_client_id", length = 255, unique = true)
    @Schema(description = "PayPal application client identifier", example = "AR_YOUR_CLIENT_ID_EXAMPLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paypalClientId;

    @Convert(converter = CryptoStringConverter.class)
    @NotBlank(message = "PayPal client secret is required")
    @Size(max = 255, message = "Client secret must not exceed 255 characters")
    @Column(name = "paypal_client_secret", length = 255) // У реальних системах цей секрет має бути зашифрований або зберігатися в безпечному сховищі!
    @Schema(description = "PayPal application client secret (should be securely stored)", example = "EH_YOUR_CLIENT_SECRET_EXAMPLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paypalClientSecret;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "paypal_merchant_id", length = 50)
    @Schema(description = "Optional: PayPal merchant account ID for specific configurations", example = "YOUR_MERCHANT_ID")
    private String paypalMerchantId;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "paypal_webhook_id", length = 255)
    @Schema(description = "PayPal webhook identifier for receiving payment notifications", example = "YOUR_WEBHOOK_ID_EXAMPLE")
    private String paypalWebhookId;

    // PayPal API URLs (for production and sandbox)
    @NotBlank(message = "PayPal API base URL is required")
    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "PayPal API URL must not exceed 500 characters")
    @Column(name = "paypal_api_base_url", length = 500)
    @Schema(description = "Base URL for PayPal REST API (e.g., https://api-m.paypal.com)", example = "https://api-m.paypal.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String paypalApiBaseUrl;

    @Column(name = "paypal_sandbox_api_base_url", length = 500)
    @Schema(description = "Base URL for PayPal Sandbox REST API (e.g., https://api-m.sandbox.paypal.com)",
            example = "https://api-m.sandbox.paypal.com")
    private String paypalSandboxApiBaseUrl;


    // Payment Gateway Redirect URLs (common for most gateways)
    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    @Column(name = "success_url", length = 500)
    @Schema(description = "URL to redirect after successful payment (handled by your application)", example = "https://shop.com/payment/success")
    private String successUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Failure URL must not exceed 500 characters")
    @Column(name = "failure_url", length = 500)
    @Schema(description = "URL to redirect after failed payment (handled by your application)", example = "https://shop.com/payment/failure")
    private String failureUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Cancel URL must not exceed 500 characters")
    @Column(name = "cancel_url", length = 500)
    @Schema(description = "URL to redirect when payment is cancelled by user (handled by your application)", example = "https://shop.com/payment/cancel")
    private String cancelUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$", message = "Invalid URL format")
    @Size(max = 500, message = "Callback URL must not exceed 500 characters")
    @Column(name = "callback_url", length = 500)
    @Schema(description = "URL for PayPal webhook notifications (e.g., payment completed, refunded)", example = "https://shop.com/api/paypal/webhook")
    private String callbackUrl;

    // Business Information for Payments (generally common across gateways)
    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name must not exceed 255 characters")
    @Column(name = "business_name", length = 255)
    @Schema(description = "Legal business name for payment processing", example = "MyStore LLC", requiredMode = Schema.RequiredMode.REQUIRED)
    private String businessName;

    @Convert(converter = CryptoStringConverter.class)
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "billing_email", length = 255)
    @Schema(description = "Email for billing notifications and PayPal account communication", example = "billing@shop.com")
    private String billingEmail;

    @Convert(converter = CryptoStringConverter.class)
    @Pattern(regexp = "^[\\d\\s()+-]+$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "billing_phone", length = 20)
    @Schema(description = "Phone number for billing support", example = "+380931234567")
    private String billingPhone;

    // Tax and Legal Information (generally common across gateways)
    @Convert(converter = CryptoStringConverter.class)
    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    @Column(name = "tax_id", length = 50)
    @Schema(description = "Tax identification number for regulatory compliance", example = "1234567890")
    private String taxId;

    @Convert(converter = CryptoStringConverter.class)
    @Size(max = 50, message = "EDRPOU must not exceed 50 characters")
    @Column(name = "edrpou", length = 50)
    @Schema(description = "Ukrainian EDRPOU code, if applicable", example = "12345678")
    private String edrpou;

    // Payment Settings (common for most gateways, but with PayPal specific considerations)
    @NotNull
    @Column(name = "default_currency", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Default currency for payments (e.g., USD, EUR). Must be supported by PayPal.", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentCurrency defaultCurrency;

    @Column(name = "minimum_payment_amount", precision = 10, scale = 2)
    @Schema(description = "Minimum payment amount allowed for a transaction", example = "1.00")
    private BigDecimal minimumPaymentAmount;

    @Column(name = "maximum_payment_amount", precision = 10, scale = 2)
    @Schema(description = "Maximum payment amount allowed for a transaction", example = "10000.00")
    private BigDecimal maximumPaymentAmount;

    @Column(name = "payment_timeout_minutes")
    @Schema(description = "Payment session timeout in minutes (e.g., how long an order link is valid)", example = "30")
    private Integer paymentTimeoutMinutes;

    // PayPal Specific Settings
    @Column(name = "is_sandbox_mode")
    @Builder.Default
    @Schema(description = "Enable PayPal sandbox mode for testing. If true, paypalSandboxApiBaseUrl will be used.", example = "false")
    private boolean isSandboxMode = false;

    @Column(name = "is_subscription_enabled")
    @Builder.Default
    @Schema(description = "Enable PayPal subscriptions and recurring payments via API", example = "false")
    private boolean isSubscriptionEnabled = false;

    @Column(name = "is_express_checkout_enabled")
    @Builder.Default
    @Schema(description = "Enable PayPal Express Checkout (Smart Payment Buttons) for faster checkout", example = "true")
    private boolean isExpressCheckoutEnabled = true;

    @Column(name = "is_credit_card_enabled")
    @Builder.Default
    @Schema(description = "Enable direct credit/debit card payments via PayPal (e.g., through PayPal's direct API or Hosted Fields)", example = "true")
    private boolean isCreditCardEnabled = true;

    // Status and Configuration (common)
    @NotNull
    @Column(name = "billing_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of billing configuration (e.g., ACTIVE, INACTIVE, PENDING)", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    private BillingStatus billingStatus;

    @Column(name = "is_test_mode") // Це поле може бути надлишковим, якщо використовується isSandboxMode
    @Builder.Default
    @Schema(description = "Legacy or general test mode flag. Consider using isSandboxMode for PayPal specific testing.", example = "false")
    private boolean isTestMode = false;

    // Audit Fields (common)
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    @Schema(description = "Timestamp when the record was created", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Schema(description = "Timestamp when the record was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Enums (updated for PayPal supported currencies)
    public enum PaymentCurrency {
        // PayPal підтримує велику кількість валют. Я додав деякі з найпоширеніших.
        // Завжди перевіряйте актуальний список підтримуваних валют PayPal API.
        @Schema(description = "US Dollar")
        USD,
        @Schema(description = "Euro")
        EUR,
        @Schema(description = "British Pound")
        GBP,
        @Schema(description = "Canadian Dollar")
        CAD,
        @Schema(description = "Australian Dollar")
        AUD,
        @Schema(description = "Japanese Yen")
        JPY,
        @Schema(description = "Swiss Franc")
        CHF,
        @Schema(description = "Swedish Krona")
        SEK,
        @Schema(description = "Norwegian Krone")
        NOK,
        @Schema(description = "Danish Krone")
        DKK,
        @Schema(description = "Polish Zloty")
        PLN,
        @Schema(description = "Czech Koruna")
        CZK,
        @Schema(description = "Hungarian Forint")
        HUF,
        @Schema(description = "Mexican Peso")
        MXN,
        @Schema(description = "Brazilian Real")
        BRL,
        @Schema(description = "Singapore Dollar")
        SGD,
        @Schema(description = "Hong Kong Dollar")
        HKD,
        @Schema(description = "New Zealand Dollar")
        NZD,
        @Schema(description = "Israeli Shekel")
        ILS,
        @Schema(description = "Philippine Peso")
        PHP,
        @Schema(description = "Thai Baht")
        THB,
        @Schema(description = "Malaysian Ringgit")
        MYR,
        @Schema(description = "Turkish Lira")
        TRY,
        @Schema(description = "Russian Ruble (limited support/sanctions considerations)")
        RUB, // Обмежена підтримка або обмеження через санкції
        @Schema(description = "Indian Rupee")
        INR,
        @Schema(description = "South African Rand")
        ZAR,
        @Schema(description = "Chinese Yuan (limited support for cross-border payments)")
        CNY // Обмежена підтримка для транскордонних платежів
    }

    public enum BillingStatus {
        @Schema(description = "Billing is active and ready for payments")
        ACTIVE,
        @Schema(description = "Billing is inactive and payments are disabled")
        INACTIVE,
        @Schema(description = "Billing is pending verification")
        PENDING,
        @Schema(description = "Billing is suspended due to issues")
        SUSPENDED,
        @Schema(description = "Billing is in test mode")
        TEST_MODE
    }
}