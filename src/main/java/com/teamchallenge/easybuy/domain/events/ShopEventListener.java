package com.teamchallenge.easybuy.domain.events;

import com.teamchallenge.easybuy.service.shop.analytics.AnalyticsService;
import com.teamchallenge.easybuy.service.shop.audit.AuditService;
import com.teamchallenge.easybuy.service.shop.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 🎧 Shop Event Listener - обработка событий магазинов
 *
 * Обрабатывает все события магазинов асинхронно:
 * - Отправка уведомлений
 * - Сбор аналитики
 * - Аудит действий
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

        try {
            // Отправляем уведомления
            notificationService.sendShopCreatedEmail(shop);

            // Собираем аналитику
            analyticsService.recordShopCreation(shop);

            // Аудит
            auditService.logShopCreation(shop);

            log.info("Shop created event processed successfully for: {}", shop.getShopName());

        } catch (Exception e) {
            log.error("Failed to process shop created event for: {}", shop.getShopName(), e);
        }
    }

    @EventListener
    @Async("shopEventExecutor")
    public void handleShopUpdated(ShopUpdatedEvent event) {
        var shop = event.getShop();

        log.info("Processing shop updated event for: {}", shop.getShopName());

        try {
            // Отправляем уведомления
            notificationService.sendShopUpdatedEmail(shop);

            // Собираем аналитику
            analyticsService.recordShopUpdate(shop);

            // Аудит
            auditService.logShopUpdate(shop);

            log.info("Shop updated event processed successfully for: {}", shop.getShopName());

        } catch (Exception e) {
            log.error("Failed to process shop updated event for: {}", shop.getShopName(), e);
        }
    }

    @EventListener
    @Async("shopEventExecutor")
    public void handleShopDeleted(ShopDeletedEvent event) {
        var shop = event.getShop();

        log.info("Processing shop deleted event for: {}", shop.getShopName());

        try {
            // Отправляем уведомления
            notificationService.sendShopDeletedEmail(shop);

            // Собираем аналитику
            analyticsService.recordShopDeletion(shop);

            // Аудит
            auditService.logShopDeletion(shop);

            log.info("Shop deleted event processed successfully for: {}", shop.getShopName());

        } catch (Exception e) {
            log.error("Failed to process shop deleted event for: {}", shop.getShopName(), e);
        }
    }
}