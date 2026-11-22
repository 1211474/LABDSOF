# Security & RBAC (applies everywhere)

- Authentication and authorization are enforced on the backend (Spring Security) — never trust a frontend-only check or a client-supplied role/ID.
- Each user can access only their own care data and support requests; providers can only manage services tied to their own organisation. Enforce this at the service layer, not just at the controller boundary.
- No PHI / medical records anywhere in the system. Care needs are expressed as coarse, non-sensitive tags only.
- Data in transit uses HTTPS; secrets/credentials are never hardcoded or logged.
- Don't weaken or bypass an existing authorization check to make a feature "work" — fix the actual permission model instead.
