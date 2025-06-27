package com.teamchallenge.easybuy.models.goods;

import com.teamchallenge.easybuy.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter // Генерує геттери
@Setter // Генерує сеттери
@Builder // Дозволяє використовувати патерн Builder
@NoArgsConstructor // Генерує конструктор без аргументів
@AllArgsConstructor // Генерує конструктор з усіма аргументами
@Table(name = "shop", indexes = { // Додаємо індекс для user_id для оптимізації запитів
        @Index(name = "idx_shop_userId", columnList = "userId")
})
@Schema(description = "Деталі про магазин на маркетплейсі.")
public class Shop {

    @Id
    @GeneratedValue // Для UUID зазвичай використовується стратегія генерації за замовчуванням (UUID)
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Унікальний ідентифікатор магазину", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotNull
    @Column(name = "shop_name", nullable = false, unique = true)
    @Schema(description = "Назва магазину", example = "Мій Супер Магазин", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Lob // Використовується для зберігання великих текстових даних
    @Column(name = "description")
    @Schema(description = "Детальний опис магазину", example = "Ми пропонуємо широкий асортимент товарів для дому та офісу.")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING) // Зберігаємо Enum як рядок у базі даних
    @Column(name = "shop_status", nullable = false)
    @Schema(description = "Поточний статус магазину", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    private ShopStatus shopStatus;

    public enum ShopStatus {
        @Schema(description = "Магазин активний і доступний", example = "ACTIVE")
        ACTIVE,
        @Schema(description = "Магазин неактивний", example = "INACTIVE")
        INACTIVE,
        @Schema(description = "Магазин очікує перевірки", example = "PENDING")
        PENDING,
        @Schema(description = "Магазин заблоковано", example = "BANNED")
        BANNED
    }

    //todo изменить на нужный
    // Зв'язок ManyToOne з User, оскільки один користувач може мати багато магазинів
    // user_id тепер буде типом UUID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    @Schema(description = "Власник магазину (користувач)", requiredMode = Schema.RequiredMode.REQUIRED)
    private User user; // Це поле представляє власника магазину

    @Column(name = "created_at", updatable = false)
    @Schema(description = "Час створення магазину", example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Час останнього оновлення магазину", example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @PrePersist
    @Schema(hidden = true) // Приховуємо цей метод у документації Swagger
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    @Schema(hidden = true) // Приховуємо цей метод у документації Swagger
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}