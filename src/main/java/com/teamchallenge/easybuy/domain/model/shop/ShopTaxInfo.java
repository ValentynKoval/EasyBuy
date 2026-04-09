package com.teamchallenge.easybuy.domain.model.shop;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Entity for storing shop tax identification and legal details.
 * Uses Shared Primary Key with the Shop entity.
 */
@Entity
@Table(name = "shop_tax_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopTaxInfo {

    @Id
    @Column(name = "shop_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "shop_id")
    @ToString.Exclude
    private Shop shop;

    @Column(name = "tax_id", nullable = false)
    private String taxId; // ИНН, EIN, VAT number

    @Enumerated(EnumType.STRING)
    @Column(name = "taxpayer_type", nullable = false)
    private TaxpayerType taxpayerType; // INDIVIDUAL or BUSINESS

    @Column(name = "legal_name", nullable = false)
    private String legalName; // Официальное название компании

    @Column(name = "tax_country_code", length = 2)
    private String taxCountryCode; // ISO код страны (напр. "US", "UA")

    @Column(name = "registered_address")
    private String registeredAddress;

    public enum TaxpayerType {
        INDIVIDUAL, BUSINESS
    }
}