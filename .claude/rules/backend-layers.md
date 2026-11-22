---
paths:
  - "care-platform-api/**"
  - "support-assistant-service/**"
---

# Backend Layering & Component Boundaries

- Each backend component follows a layered architecture: `controller` → `service` → `repository` (+ `integration`/`config` for external clients). Keep controllers thin — delegate business rules to the service layer, not the other way around.
- The Care Platform API and the Support Assistant Service are independently deployable Spring Boot applications. Never call into another component's internal classes, services, or repositories directly.
- The Care Platform API is the sole owner of the authoritative database (caregivers, care recipients, organisations, services, support requests). The Support Assistant Service accesses this data only through the Care Platform API's REST endpoints — never via direct DB access or shared entities.
- Cross-component communication happens over explicit, versioned REST contracts, not shared internal types.
- Keep infrastructure/integration code (DB access, LLM client, notification delivery) separate from business-logic/service classes.
