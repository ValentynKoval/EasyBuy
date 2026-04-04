package com.teamchallenge.easybuy.service.shop.notification;


import com.teamchallenge.easybuy.domain.model.shop.Shop;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.server.url}")
    private String frontendUrl;

    @Value("${easybuy.admin.emails:}")
    private String[] adminEmails;

    // ==================== SHOP EMAILS ====================

    @Async("notificationTaskExecutor")
    public CompletableFuture<Void> sendShopCreatedEmail(Shop shop) {
        if (!isValidShopForEmail(shop)) return CompletableFuture.completedFuture(null);

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("shopName", shop.getShopName());
            model.put("status", shop.getShopStatus());
            model.put("description", shop.getShopDescription());
            model.put("frontendUrl", frontendUrl);

            String html = processTemplate("email/shop_created.html", model);
            sendEmail(shop.getSeller().getEmail(), "Магазин успешно создан - " + shop.getShopName(), html);

            sendNewShopNotificationToAdmins(shop);

        } catch (IOException | TemplateException | MessagingException e) {
            log.error("Failed to send shop created email for: {}", shop.getShopName(), e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("notificationTaskExecutor")
    public CompletableFuture<Void> sendShopUpdatedEmail(Shop shop) {
        if (!isValidShopForEmail(shop)) return CompletableFuture.completedFuture(null);

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("shopName", shop.getShopName());
            model.put("status", shop.getShopStatus());
            model.put("shopId", shop.getShopId());
            model.put("frontendUrl", frontendUrl);

            String html = processTemplate("email/shop_updated.html", model);
            sendEmail(shop.getSeller().getEmail(), "Магазин обновлен - " + shop.getShopName(), html);

        } catch (IOException | TemplateException | MessagingException e) {
            log.error("Failed to send shop updated email for: {}", shop.getShopName(), e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("notificationTaskExecutor")
    public CompletableFuture<Void> sendShopDeletedEmail(Shop shop) {
        if (!isValidShopForEmail(shop)) return CompletableFuture.completedFuture(null);

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("shopName", shop.getShopName());

            String html = processTemplate("email/shop_deleted.html", model);
            sendEmail(shop.getSeller().getEmail(), "Магазин удален - " + shop.getShopName(), html);

        } catch (IOException | TemplateException | MessagingException e) {
            log.error("Failed to send shop deleted email for: {}", shop.getShopName(), e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("notificationTaskExecutor")
    public CompletableFuture<Void> sendShopStatusChangedEmail(Shop shop, Shop.ShopStatus oldStatus) {
        if (!isValidShopForEmail(shop)) return CompletableFuture.completedFuture(null);

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("shopName", shop.getShopName());
            model.put("oldStatus", oldStatus);
            model.put("newStatus", shop.getShopStatus());
            model.put("statusMessage", getStatusChangeMessage(shop.getShopStatus()));
            model.put("shopId", shop.getShopId());
            model.put("frontendUrl", frontendUrl);

            String html = processTemplate("email/shop_status_changed.html", model);
            sendEmail(shop.getSeller().getEmail(), "Статус магазина изменен - " + shop.getShopName(), html);

        } catch (IOException | TemplateException | MessagingException e) {
            log.error("Failed to send shop status changed email for: {}", shop.getShopName(), e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }

        return CompletableFuture.completedFuture(null);
    }

    // ==================== ADMIN NOTIFICATIONS ====================

    @Async("notificationTaskExecutor")
    public CompletableFuture<Void> sendNewShopNotificationToAdmins(Shop shop) {
        if (adminEmails == null || adminEmails.length == 0) {
            log.warn("No admin emails configured. Cannot send new shop notification.");
            return CompletableFuture.completedFuture(null);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("shopName", shop.getShopName());
        model.put("sellerEmail", shop.getSeller().getEmail());

        try {
            String html = processTemplate("email/new_shop_admin_notification.html", model);

            CompletableFuture<?>[] futures = Arrays.stream(adminEmails)
                    .map(email -> CompletableFuture.runAsync(() -> {
                        try {
                            sendEmail(email, "Новый магазин создан - " + shop.getShopName(), html);
                        } catch (MessagingException e) {
                            log.error("Failed to send new shop notification to admin: {}", email, e);
                        }
                    })).toArray(CompletableFuture[]::new);

            return CompletableFuture.allOf(futures);

        } catch (IOException | TemplateException e) {
            log.error("Failed to generate new shop admin email", e);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    // ==================== PRIVATE HELPERS ====================

    private boolean isValidShopForEmail(Shop shop) {
        if (shop == null || shop.getSeller() == null || shop.getSeller().getEmail() == null) {
            log.warn("Cannot send email: shop, seller or email is null.");
            return false;
        }
        return true;
    }

    private void sendEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
        log.debug("Email sent to: {} with subject: {}", to, subject);
    }

    private String processTemplate(String templateName, Map<String, Object> model) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(model, writer);
        return writer.toString();
    }

    private String getStatusChangeMessage(Shop.ShopStatus status) {
        return switch (status) {
            case ACTIVE -> "Поздравляем! Ваш магазин активен и доступен покупателям.";
            case INACTIVE -> "Ваш магазин временно неактивен.";
            case PENDING -> "Ваш магазин находится на рассмотрении.";
            case BANNED -> "Ваш магазин заблокирован из-за нарушений политики.";
            case REJECTED -> "К сожалению, ваш магазин был отклонен. Проверьте комментарии модератора и внесите исправления.";
        };
    }
}