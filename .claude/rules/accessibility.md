---
paths:
  - "frontend/**"
---

# Accessibility & UI Rules (Frontend)

- Mobile-first, accessibility-first: large readable text, high contrast, large touch targets, plain language, minimal steps.
- Every service result must show a prominent "Call / Contact" action and must never present availability as guaranteed.
- No mandatory medical-document uploads anywhere in the UI.
- The frontend must never call the LLM provider directly — all AI requests go through the Support Assistant Service's REST API.
- Treat any auth/role state in the UI as advisory only; the backend is the sole source of truth for what a user can see or do. Don't hide a disabled action instead of relying on the backend to reject it.
