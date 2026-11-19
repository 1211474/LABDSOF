---
paths:
  - "care-platform-api/**"
---

# Support Request Notifications — Transactional Outbox

There is no message broker in this project. Asynchronous notifications to a caregiver's backup contact after a support request is created use a **transactional outbox** pattern:

- When a support request is created, write a corresponding outbox row in the *same database transaction* as the support request itself — never notify "fire and forget" outside that transaction.
- A background process polls/consumes pending outbox rows and performs the actual delivery (SMS/email/push).
- Delivery failures are retried a bounded number of times, not indefinitely.
- Consumption/delivery must be idempotent — a retried or redelivered outbox row must never result in a duplicate notification to the recipient (e.g. mark rows as sent/processed atomically with delivery, use a unique delivery key per row).
- Don't introduce a message broker or distributed queue for this flow unless a new, clearly justified requirement arises — this keeps the Care Platform API's failure/retry model self-contained.
