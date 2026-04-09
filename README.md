This branch implements Shop module

As part of the Shop module refinement, I have implemented full CRUD functionality for the Shop entity. This update aligns endpoints with business roles (BUYER / SELLER / MANAGER / ADMIN) and strengthens ownership validation (e.g., handling "foreign shop -> 403" vs. "own shop -> 200" scenarios). The store profile editing logic has also been streamlined into a single flow (PUT /shops/{id}/profile).

Key changes:



Testing: Added and updated integration and service tests for all related entities (Shop, contact/billing/tax/seo), including specific tests for mail and event resilience.

Tech Debt: Resolved warnings and deprecated code (e.g., UUID generator), fixed naming issues, and unified Lombok usage.

API & Docs: Improved DTOs for create/update/patch operations with stricter validation and updated Swagger/Javadoc documentation.

Summary: The Shop module is now MVP/pre-prod ready, focusing on robust access control, stability, and API predictability.