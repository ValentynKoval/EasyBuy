package com.teamchallenge.easybuy.domain.model.shop;

import com.teamchallenge.easybuy.persistence.converter.CryptoStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "shop_billing_info", indexes = {
        @Index(name = "idx_billing_info_shop_id", columnList = "shop_id"),
        @Index(name = "idx_billing_info_status", columnList = "billing_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "Contains billing and payment gateway information for a shop integration with PayPal.")
public class ShopBillingInfo {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "billing_info_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include // ИСПРАВЛЕНО: Сравнение будет только по ID
    @Schema(description = "Unique identifier for the billing info record. Read only",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID billingInfoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    @ToString.Exclude
    @Schema(description = "The shop this billing info belongs to")
    private Shop shop;

    // PayPal Configuration
    @Convert(converter = CryptoStringConverter.class)
    @Size(max = 255, message = "Client ID must not exceed 255 characters")
    @Column(name = "paypal_client_id", length = 255, unique = true)
    @Schema(description = "PayPal application client identifier", example = "AR_YOUR_CLIENT_ID_EXAMPLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paypalClientId;

    @Convert(converter = CryptoStringConverter.class)
    @ToString.Exclude
    @Size(max = 255, message = "Client secret must not exceed 255 characters")
    @Column(name = "paypal_client_secret", length = 255)
    @Schema(description = "PayPal application client secret (should be securely stored)", example = "EH_YOUR_CLIENT_SECRET_EXAMPLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paypalClientSecret;

    // ... остальные поля оставляем без изменений ...

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "paypal_merchant_id", length = 50)
    private String paypalMerchantId;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "paypal_webhook_id", length = 255)
    private String paypalWebhookId;

    @NotBlank(message = "PayPal API base URL is required")
    @Column(name = "paypal_api_base_url", length = 500)
    private String paypalApiBaseUrl;

    @Column(name = "paypal_sandbox_api_base_url", length = 500)
    private String paypalSandboxApiBaseUrl;

    @Column(name = "success_url", length = 500)
    private String successUrl;

    @Column(name = "failure_url", length = 500)
    private String failureUrl;

    @Column(name = "cancel_url", length = 500)
    private String cancelUrl;

    @Column(name = "callback_url", length = 500)
    private String callbackUrl;

    @NotBlank(message = "Business name is required")
    @Column(name = "business_name", length = 255)
    private String businessName;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "billing_email", length = 255)
    private String billingEmail;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "billing_phone", length = 20)
    private String billingPhone;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Convert(converter = CryptoStringConverter.class)
    @Column(name = "edrpou", length = 50)
    private String edrpou;

    @NotNull
    @Column(name = "default_currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentCurrency defaultCurrency;

    @Column(name = "minimum_payment_amount", precision = 10, scale = 2)
    private BigDecimal minimumPaymentAmount;

    @Column(name = "maximum_payment_amount", precision = 10, scale = 2)
    private BigDecimal maximumPaymentAmount;

    @Column(name = "payment_timeout_minutes")
    private Integer paymentTimeoutMinutes;

    @Column(name = "is_sandbox_mode")
    @Builder.Default
    private boolean isSandboxMode = false;

    @Column(name = "is_subscription_enabled")
    @Builder.Default
    private boolean isSubscriptionEnabled = false;

    @Column(name = "is_express_checkout_enabled")
    @Builder.Default
    private boolean isExpressCheckoutEnabled = true;

    @Column(name = "is_credit_card_enabled")
    @Builder.Default
    private boolean isCreditCardEnabled = true;

    @NotNull
    @Column(name = "billing_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingStatus billingStatus;

    @Column(name = "is_test_mode")
    @Builder.Default
    private boolean isTestMode = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum PaymentCurrency {
        USD, EUR, GBP, CAD, AUD, JPY, CHF, SEK, NOK, DKK, PLN, CZK, HUF, MXN, BRL, SGD, HKD, NZD, ILS, PHP, THB, MYR,
        TRY, RUB, INR, ZAR, CNY
    }

    public enum BillingStatus {
        ACTIVE, INACTIVE, PENDING, SUSPENDED, TEST_MODE
    }
}