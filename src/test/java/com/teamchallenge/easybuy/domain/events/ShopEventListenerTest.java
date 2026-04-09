package com.teamchallenge.easybuy.domain.events;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.service.shop.analytics.AnalyticsService;
import com.teamchallenge.easybuy.service.shop.audit.AuditService;
import com.teamchallenge.easybuy.service.shop.notification.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShopEventListenerTest {

    @Mock
    private NotificationService notificationService;
    @Mock
    private AnalyticsService analyticsService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private ShopEventListener listener;

    @Test
    @DisplayName("handleShopCreated should swallow notification failures and not propagate")
    void handleShopCreated_notificationFailure_shouldNotThrow() {
        Shop shop = testShop("Created Shop");
        ShopCreatedEvent event = new ShopCreatedEvent(shop);

        doThrow(new RuntimeException("mail failed"))
                .when(notificationService).sendShopCreatedEmail(shop);

        assertDoesNotThrow(() -> listener.handleShopCreated(event));
        verify(analyticsService, never()).recordShopCreation(shop);
        verify(auditService, never()).logShopCreation(shop);
    }

    @Test
    @DisplayName("handleShopUpdated should swallow analytics failures and not propagate")
    void handleShopUpdated_analyticsFailure_shouldNotThrow() {
        Shop shop = testShop("Updated Shop");
        ShopUpdatedEvent event = new ShopUpdatedEvent(shop);

        doThrow(new RuntimeException("analytics failed"))
                .when(analyticsService).recordShopUpdate(shop);

        assertDoesNotThrow(() -> listener.handleShopUpdated(event));
        verify(notificationService).sendShopUpdatedEmail(shop);
        verify(auditService, never()).logShopUpdate(shop);
    }

    @Test
    @DisplayName("handleShopDeleted should swallow audit failures and not propagate")
    void handleShopDeleted_auditFailure_shouldNotThrow() {
        Shop shop = testShop("Deleted Shop");
        ShopDeletedEvent event = new ShopDeletedEvent(shop);

        doThrow(new RuntimeException("audit failed"))
                .when(auditService).logShopDeletion(shop);

        assertDoesNotThrow(() -> listener.handleShopDeleted(event));
        verify(notificationService).sendShopDeletedEmail(shop);
        verify(analyticsService).recordShopDeletion(shop);
    }

    private Shop testShop(String name) {
        Shop shop = new Shop();
        shop.setShopName(name);
        return shop;
    }
}

