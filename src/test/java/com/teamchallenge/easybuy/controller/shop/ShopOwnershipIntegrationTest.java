package com.teamchallenge.easybuy.controller.shop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.IntegrationTest;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.domain.model.shop.ShopBillingInfo;
import com.teamchallenge.easybuy.domain.model.shop.ShopContactInfo;
import com.teamchallenge.easybuy.domain.model.shop.ShopSeoSettings;
import com.teamchallenge.easybuy.domain.model.shop.ShopTaxInfo;
import com.teamchallenge.easybuy.domain.model.user.Role;
import com.teamchallenge.easybuy.domain.model.user.Seller;
import com.teamchallenge.easybuy.repository.shop.ShopRepository;
import com.teamchallenge.easybuy.repository.shop.shopbillinginfo.ShopBillingRepository;
import com.teamchallenge.easybuy.repository.shop.shopcontact.ShopContactInfoRepository;
import com.teamchallenge.easybuy.repository.shop.shopseosettings.ShopSeoSettingsRepository;
import com.teamchallenge.easybuy.repository.shop.shoptaxrepository.ShopTaxRepository;
import com.teamchallenge.easybuy.repository.user.seller.SellerRepository;
import com.teamchallenge.easybuy.service.shop.analytics.AnalyticsService;
import com.teamchallenge.easybuy.service.shop.audit.AuditService;
import com.teamchallenge.easybuy.service.shop.notification.NotificationService;
import com.teamchallenge.easybuy.service.payment.StripeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@MockitoBean(types = org.springframework.data.redis.connection.RedisConnectionFactory.class)
class ShopOwnershipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopContactInfoRepository contactInfoRepository;

    @Autowired
    private ShopTaxRepository taxRepository;

    @Autowired
    private ShopSeoSettingsRepository seoRepository;

    @Autowired
    private ShopBillingRepository billingRepository;

    @MockitoBean
    private StripeService stripeService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private AuditService auditService;

    @BeforeEach
    void setUpEventServiceStubs() {
        when(notificationService.sendShopUpdatedEmail(any(Shop.class))).thenReturn(CompletableFuture.completedFuture(null));
        when(analyticsService.recordShopUpdate(any(Shop.class))).thenReturn(CompletableFuture.completedFuture(null));
        when(auditService.logShopUpdate(any(Shop.class))).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    @WithMockUser(username = "seller-owner@example.com", roles = {"SELLER"})
    void sellerOwnShop_update_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-owner@example.com", "Owner Seller");
        Shop ownShop = createShop(owner, "Owner Shop");

        mockMvc.perform(put("/api/v1/shops/{id}", ownShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validShopUpdateBody(ownShop, owner, "Updated desc by owner"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller-a@example.com", roles = {"SELLER"})
    void sellerForeignShop_update_shouldReturn403() throws Exception {
        Seller sellerA = createSeller("seller-a@example.com", "Seller A");
        Seller sellerB = createSeller("seller-b@example.com", "Seller B");
        Shop foreignShop = createShop(sellerB, "Foreign Shop");

        mockMvc.perform(put("/api/v1/shops/{id}", foreignShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validShopUpdateBody(foreignShop, sellerA, "attempt"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "seller-profile@example.com", roles = {"SELLER"})
    void sellerOwnShop_profileUpdate_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-profile@example.com", "Profile Seller");
        Shop ownShop = createShop(owner, "Profile Shop");

        mockMvc.perform(put("/api/v1/shops/{id}/profile", ownShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validShopUpdateBody(ownShop, owner, "profile-updated"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller-profile-a@example.com", roles = {"SELLER"})
    void sellerForeignShop_profileUpdate_shouldReturn403() throws Exception {
        Seller sellerA = createSeller("seller-profile-a@example.com", "Seller Profile A");
        Seller sellerB = createSeller("seller-profile-b@example.com", "Seller Profile B");
        Shop foreignShop = createShop(sellerB, "Profile Foreign Shop");

        mockMvc.perform(put("/api/v1/shops/{id}/profile", foreignShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validShopUpdateBody(foreignShop, sellerA, "attempt"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "seller-contact-owner@example.com", roles = {"SELLER"})
    void sellerOwnShop_contactUpdate_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-contact-owner@example.com", "Contact Owner");
        Shop ownShop = createShop(owner, "Contact Shop");
        createContactInfo(ownShop);

        mockMvc.perform(put("/api/v1/shops/{id}/contact-info", ownShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContactUpdateBody("owner+contact@example.com"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller-contact-a@example.com", roles = {"SELLER"})
    void sellerForeignShop_contactUpdate_shouldReturn403() throws Exception {
        createSeller("seller-contact-a@example.com", "Seller Contact A");
        Seller sellerB = createSeller("seller-contact-b@example.com", "Seller Contact B");
        Shop foreignShop = createShop(sellerB, "Foreign Contact Shop");
        createContactInfo(foreignShop);

        mockMvc.perform(put("/api/v1/shops/{id}/contact-info", foreignShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContactUpdateBody("attempt@example.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "seller-tax-owner@example.com", roles = {"SELLER"})
    void sellerOwnShop_taxUpdate_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-tax-owner@example.com", "Tax Owner");
        Shop ownShop = createShop(owner, "Tax Shop");
        createTaxInfo(ownShop);

        mockMvc.perform(put("/api/v1/shops/{id}/tax-info", ownShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaxUpdateBody("OWNER-TAX-123"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller-tax-a@example.com", roles = {"SELLER"})
    void sellerForeignShop_taxUpdate_shouldReturn403() throws Exception {
        createSeller("seller-tax-a@example.com", "Seller Tax A");
        Seller sellerB = createSeller("seller-tax-b@example.com", "Seller Tax B");
        Shop foreignShop = createShop(sellerB, "Foreign Tax Shop");
        createTaxInfo(foreignShop);

        mockMvc.perform(put("/api/v1/shops/{id}/tax-info", foreignShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaxUpdateBody("FOREIGN-TAX-123"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "seller-seo-owner@example.com", roles = {"SELLER"})
    void sellerOwnShop_seoUpdate_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-seo-owner@example.com", "Seo Owner");
        Shop ownShop = createShop(owner, "Seo Shop");
        createSeoSettings(ownShop);

        mockMvc.perform(put("/api/v1/shops/{id}/seo-settings", ownShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSeoUpdateBody("Owner SEO title"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller-seo-a@example.com", roles = {"SELLER"})
    void sellerForeignShop_seoUpdate_shouldReturn403() throws Exception {
        createSeller("seller-seo-a@example.com", "Seller Seo A");
        Seller sellerB = createSeller("seller-seo-b@example.com", "Seller Seo B");
        Shop foreignShop = createShop(sellerB, "Foreign Seo Shop");
        createSeoSettings(foreignShop);

        mockMvc.perform(put("/api/v1/shops/{id}/seo-settings", foreignShop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSeoUpdateBody("Foreign SEO title"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "seller-billing-owner@example.com", roles = {"SELLER"})
    void sellerOwnShop_getBilling_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-billing-owner@example.com", "Billing Owner");
        Shop ownShop = createShop(owner, "Billing Shop");
        createBillingInfo(ownShop, owner.getEmail());

        mockMvc.perform(get("/api/v1/shops/{id}/billing", ownShop.getShopId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seller-billing-a@example.com", roles = {"SELLER"})
    void sellerForeignShop_getBilling_shouldReturn403() throws Exception {
        createSeller("seller-billing-a@example.com", "Billing A");
        Seller sellerB = createSeller("seller-billing-b@example.com", "Billing B");
        Shop foreignShop = createShop(sellerB, "Foreign Billing Shop");
        createBillingInfo(foreignShop, sellerB.getEmail());

        mockMvc.perform(get("/api/v1/shops/{id}/billing", foreignShop.getShopId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "seller-billing-owner2@example.com", roles = {"SELLER"})
    void sellerOwnShop_billingOnboarding_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-billing-owner2@example.com", "Billing Owner2");
        Shop ownShop = createShop(owner, "Billing Onboarding Shop");
        // Keep billing absent: onboarding flow should initialize it for owner shop.
        when(stripeService.createOnboardingLink(anyString())).thenReturn("https://connect.stripe.com/test");
        when(stripeService.createStripeAccount(anyString())).thenReturn("acct_test_owner");

        mockMvc.perform(post("/api/v1/shops/{id}/billing/onboarding", ownShop.getShopId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin-billing@example.com", roles = {"ADMIN"})
    void adminAnyShop_getBilling_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-admin-billing-owned@example.com", "Owner Billing");
        Shop shop = createShop(owner, "Admin Billing Target Shop");
        createBillingInfo(shop, owner.getEmail());

        mockMvc.perform(get("/api/v1/shops/{id}/billing", shop.getShopId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void adminAnyShop_update_shouldReturn200() throws Exception {
        Seller owner = createSeller("seller-admin-owned@example.com", "Owner");
        Shop shop = createShop(owner, "Admin Target Shop");

        mockMvc.perform(put("/api/v1/shops/{id}", shop.getShopId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validShopUpdateBody(shop, owner, "updated-by-admin"))))
                .andExpect(status().isOk());
    }

    private Seller createSeller(String email, String name) {
        Seller seller = new Seller();
        seller.setEmail(email);
        seller.setPassword("encoded-password");
        seller.setName(name);
        seller.setRole(Role.SELLER);
        return sellerRepository.save(seller);
    }

    private Shop createShop(Seller seller, String shopName) {
        Shop shop = new Shop();
        shop.setShopName(shopName + "-" + UUID.randomUUID());
        shop.setShopDescription("Initial description");
        shop.setShopStatus(Shop.ShopStatus.ACTIVE);
        shop.setSeller(seller);
        shop.setCurrency("UAH");
        shop.setLanguage("uk");
        shop.setTimezone("Europe/Kyiv");
        return shopRepository.save(shop);
    }

    private Map<String, Object> validShopUpdateBody(Shop shop, Seller seller, String description) {
        Map<String, Object> body = new HashMap<>();
        body.put("shopName", shop.getShopName());
        body.put("shopDescription", description);
        body.put("shopStatus", shop.getShopStatus().name());
        body.put("sellerId", seller.getId());
        body.put("currency", "UAH");
        body.put("language", "uk");
        body.put("timezone", "Europe/Kyiv");
        return body;
    }

    private Map<String, Object> validContactUpdateBody(String email) {
        Map<String, Object> body = new HashMap<>();
        body.put("contactEmail", email);
        body.put("contactPhone", "+380931112233");
        body.put("businessAddress", "Kyiv, Main st. 1");
        body.put("city", "Kyiv");
        body.put("country", "UA");
        return body;
    }

    private Map<String, Object> validTaxUpdateBody(String taxId) {
        Map<String, Object> body = new HashMap<>();
        body.put("taxId", taxId);
        body.put("taxpayerType", "BUSINESS");
        body.put("legalName", "EasyBuy LLC");
        body.put("taxCountryCode", "UA");
        body.put("registeredAddress", "Kyiv, Main st. 1");
        return body;
    }

    private Map<String, Object> validSeoUpdateBody(String metaTitle) {
        Map<String, Object> body = new HashMap<>();
        body.put("metaTitle", metaTitle);
        body.put("metaDescription", "Updated SEO description");
        body.put("metaKeywords", "shop, seo");
        body.put("canonicalUrl", "https://example.com/shop");
        return body;
    }

    private void createContactInfo(Shop shop) {
        ShopContactInfo info = new ShopContactInfo();
        info.setShop(shop);
        info.setContactEmail("contact-" + UUID.randomUUID() + "@example.com");
        info.setContactPhone("+380931234567");
        info.setBusinessAddress("Kyiv, Main st. 1");
        info.setCity("Kyiv");
        info.setCountry("UA");
        info.setActive(true);
        contactInfoRepository.save(info);
    }

    private void createTaxInfo(Shop shop) {
        ShopTaxInfo taxInfo = new ShopTaxInfo();
        taxInfo.setShop(shop);
        taxInfo.setTaxId("TAX-" + UUID.randomUUID());
        taxInfo.setTaxpayerType(ShopTaxInfo.TaxpayerType.BUSINESS);
        taxInfo.setLegalName("EasyBuy Tax Legal Name");
        taxInfo.setTaxCountryCode("UA");
        taxInfo.setRegisteredAddress("Kyiv, Main st. 1");
        taxRepository.save(taxInfo);
    }

    private void createSeoSettings(Shop shop) {
        ShopSeoSettings seo = new ShopSeoSettings();
        seo.setShop(shop);
        seo.setMetaTitle("Initial SEO title");
        seo.setMetaDescription("Initial SEO description");
        seo.setCanonicalUrl("https://example.com/initial-shop");
        seo.calculateSeoScore();
        seoRepository.save(seo);
    }

    private void createBillingInfo(Shop shop, String email) {
        ShopBillingInfo billingInfo = new ShopBillingInfo();
        billingInfo.setShop(shop);
        billingInfo.setBillingEmail(email);
        billingInfo.setStripeAccountId("acct_" + UUID.randomUUID());
        billingRepository.save(billingInfo);
    }
}

