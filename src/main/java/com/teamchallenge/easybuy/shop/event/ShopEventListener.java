package com.teamchallenge.easybuy.shop.event;

import com.teamchallenge.easybuy.shop.service.analytics.AnalyticsService;
import com.teamchallenge.easybuy.shop.service.audit.AuditService;
import com.teamchallenge.easybuy.shop.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Shop Event Listener - processing store events
 * *
 * * Processes all store events asynchronously:
 * * - Sending notifications
 * * - Collecting analytics
 * * - Auditing actions
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShopEventListener {

    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;
    private final AuditService auditService;

    @EventListener
    @Async("shopEventExecutor")
    public void handleShopCreated(ShopCreatedEvent event) {
        var shop = event.getShop();

        log.info("Processing shop created event for: {}", shop.getShopName());

        processEventAsync(
                "created",
                shop.getShopName(),
                notificationService.sendShopCreatedEmail(shop),
                analyticsService.recordShopCreation(shop),
                auditService.logShopCreation(shop)
        );
    }

    @EventListener
    @Async("shopEventExecutor")
    public void handleShopUpdated(ShopUpdatedEvent event) {
        var shop = event.getShop();

        log.info("Processing shop updated event for: {}", shop.getShopName());

        processEventAsync(
                "updated",
                shop.getShopName(),
                notificationService.sendShopUpdatedEmail(shop),
                analyticsService.recordShopUpdate(shop),
                auditService.logShopUpdate(shop)
        );
    }

    @EventListener
    @Async("shopEventExecutor")
    public void handleShopDeleted(ShopDeletedEvent event) {
        var shop = event.getShop();

        log.info("Processing shop deleted event for: {}", shop.getShopName());

        processEventAsync(
                "deleted",
                shop.getShopName(),
                notificationService.sendShopDeletedEmail(shop),
                analyticsService.recordShopDeletion(shop),
                auditService.logShopDeletion(shop)
        );
    }

    private void processEventAsync(String eventType, String shopName, CompletableFuture<Void>... tasks) {
        CompletableFuture.allOf(tasks)
                .whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to process shop {} event for: {}", eventType, shopName, throwable);
                        return;
                    }
                    log.info("Shop {} event processed successfully for: {}", eventType, shopName);
                });
    }
}