This branch implements Shop module 

# EasyBuy - Feature-Based Architecture ✅

**Статус миграции:** ✅ Feature-based packaging полностью завершена (2026-04-14)

## Архитектура

Проект использует **feature-based packaging** вместо слоистой архитектуры:

```
src/main/java/com/teamchallenge/easybuy/
├── common/          # Общие DTOs, маппинги, конфиги
├── security/        # JWT, Security конфиг
├── user/            # User domain
├── auth/            # Authentication & Authorization
├── shop/            # Shop domain (controller, service, repository, mapper, domain)
├── product/         # Product/Goods domain
├── infrastructure/  # Mail, Cloudinary, Cache
└── payment/         # Stripe интеграция
```

Это дает лучшую модульность, масштабируемость и упрощает добавление новых features.

**Подробнее:** см. [MIGRATION_COMPLETE.md](./MIGRATION_COMPLETE.md) и [AGENTS.md](./AGENTS.md)

## Shop Module

As part of the Shop module refinement, full CRUD functionality for the Shop entity has been implemented. This aligns endpoints with business roles (BUYER / SELLER / MANAGER / ADMIN) and strengthens ownership validation (e.g., handling "foreign shop -> 403" vs. "own shop -> 200" scenarios). The store profile editing logic has been streamlined into a single flow (PUT /shops/{id}/profile).

Key changes:



Testing: Added and updated integration and service tests for all related entities (Shop, contact/billing/tax/seo), including specific tests for mail and event resilience.

Tech Debt: Resolved warnings and deprecated code (e.g., UUID generator), fixed naming issues, and unified Lombok usage.

API & Docs: Improved DTOs for create/update/patch operations with stricter validation and updated Swagger/Javadoc documentation.

Summary: The Shop module is now MVP/pre-prod ready, focusing on robust access control, stability, and API predictability.

## Local environment setup

1. Copy `.env.example` to `.env`.
2. Set your local secret values in `.env`.
3. Do not commit `.env`.
