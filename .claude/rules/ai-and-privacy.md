---
paths:
  - "support-assistant-service/**"
---

# AI & Privacy Rules (Support Assistant Service)

- Only Tier 3 (non-sensitive, public/operational) data may be sent to the external LLM provider. Tier 1 data (credentials, exact addresses) and Tier 2 data (PII: emails, phone numbers, task descriptions, accessibility tags) must never leave the system boundary.
- Validate/scrub outgoing LLM payloads against an explicit field allowlist before sending — don't rely on prompt instructions to keep the LLM from using sensitive fields.
- Never access the Care Platform API's database directly. All care/service data must come through the Care Platform API's published REST endpoints.
- Only retrieve and summarize data the authenticated caller is authorized to see — never broaden scope beyond what the Care Platform API would return to that specific user.
- Refuse medical diagnosis, treatment, or emergency-decision requests; redirect to human/institutional emergency contacts instead of answering.
- Every AI response must include source context, a limitation notice, and a human/institutional contact action — never present a summary as a confirmed, current fact.
- If the LLM provider or the Care Platform API is unavailable, fail with a controlled error or degrade to standard search — never fabricate an answer or present unverified/cached data as current.
- Don't log full prompts or LLM responses that contain personal data; keep only what's needed to diagnose failures.
