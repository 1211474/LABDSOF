# CareBnB

Help informal caregivers quickly find a reliable local service alternative and coordinate backup support when their normal care arrangements are disrupted.

## Stack & Architecture

- **Frontend:** React + Tailwind CSS (`frontend/`).
- **Backend:** two independently deployable Spring Boot / Maven applications:
  - `care-platform-api/` — **Care Platform API**: core care workflows, the authoritative system of record (caregivers, care recipients, organisations, support services, support requests). Owns the PostgreSQL database.
  - `support-assistant-service/` — **Support Assistant Service**: AI-assisted plain-language service navigation. Calls the Care Platform API for verified data; never touches its database.
- Each backend component uses a layered architecture internally: `controller` → `service` → `repository` (+ `dto`, `entity`, `security`, `integration`/`config` for cross-cutting/infra concerns). Keep controllers thin; business rules live in the service layer.
- **Database:** PostgreSQL, owned exclusively by the Care Platform API.
- **Communication:** REST over HTTPS — frontend ↔ both backends, and Support Assistant Service → Care Platform API.

## Asynchronous Notifications (no message broker)

Notifications to a caregiver's backup contact after a support request is created use a **transactional outbox** pattern instead of a message broker:
- An outbox row is written in the Care Platform API's database in the *same transaction* as the support request itself.
- A background process polls/consumes pending outbox rows and performs the actual delivery.
- Retries are bounded (not indefinite) on delivery failure.
- Delivery is idempotent — retries or redeliveries must never produce duplicate notifications.

See `.claude/rules/outbox-notifications.md` for implementation rules.

## Inviolable Rules

- Authentication & authorization are enforced on the **backend** (Spring Security) — never trust the frontend as the source of truth for access control.
- **No PHI.** Care needs are expressed only as coarse, non-sensitive tags; no medical documents or clinical records are stored anywhere.
- The **frontend never calls the LLM provider directly** — all AI traffic is mediated by the Support Assistant Service.
- The **Support Assistant Service never accesses the Care Platform API's database directly** — only through its published REST API, and only data the authenticated caller is authorized to see.
- Only **Tier 3** (non-sensitive, public/operational) data may reach the external LLM. Tier 1 (restricted) and Tier 2 (PII) data must never leave the system boundary.
- **No availability guarantees** — every service result must state that availability should be confirmed directly with the institution.
- Every service result and AI recommendation includes a prominent **"Call / Contact"** action.
- The AI must refuse medical/emergency requests and redirect to human/institutional contacts.

## Process Conventions

- Branches: `feature/<short-descriptive-name>` (no issue tracker in use — pick a short, descriptive name).
- Commits: prefixed `feat:`, `fix:`, `chore:`, `docs:` + short description.
- Every change goes through a pull request with at least 1 reviewer.
- New code: ≥80% test coverage.
- Definition of Done: code reviewed and merged with no conflicts, tests passing, CI green, docs updated.

## Build & Test Commands

- Care Platform API: `mvn -f care-platform-api/pom.xml verify`
- Support Assistant Service: `mvn -f support-assistant-service/pom.xml verify`
- Frontend: `cd frontend && npm ci && npm test && npm run build`
- Full stack locally: `docker compose up --build` (see `README.md` for ports and local-dev instructions without Docker).
- Coverage reports: `care-platform-api/target/site/jacoco/index.html`, `support-assistant-service/target/site/jacoco/index.html`.
- CI runs the same build+test commands for all three components plus a Docker Compose smoke test: `.github/workflows/ci.yml`.

For Claude Code sessions, the equivalent repeatable actions are available as skills:
`run-app` (start/stop the stack), `run-tests` (run all three test suites), and `verify-app`
(end-to-end smoke check mirroring the CI job) — see `.claude/skills/`.

## Where to Look in docs/geral.md

| Topic | Section(s) |
|---|---|
| Component boundaries & internal layering | ADR-003, ADR-004, §8.2 |
| Auth & RBAC | ADR-007, Security and Privacy Assessment §3 |
| AI integration, scope & guardrails | ADR-008, ADR-009, Responsible AI Opportunity Assessment |
| Privacy, data tiers, retention | ADR-010, Security and Privacy Assessment §2, §5 |
| Failure handling & resilience | ADR-011 |
| Non-functional requirements | Non-Functional Requirements (FURPS+) |
| Personas & user journeys | Personas, User Journeys |
| Backlog, epics, release plan | Product Backlog, Epics, Release Plan |

## TODO — Not Yet Defined

- CI (`.github/workflows/ci.yml`) builds, tests, and runs a Docker Compose smoke test for all three components. Both Maven modules already fail `mvn verify` below 80% line coverage (JaCoCo `check`, bound to the `verify` phase); the frontend has no equivalent coverage gate yet.
- Definition of Done is a written agreement, not fully enforced by tooling yet (required checks on the PR, lint, frontend coverage gate).
- Service-to-service authentication between the two backend components — see Open Decisions below.
- External data source for enriching service information beyond provider-submitted updates — see Open Decisions below.
- Retention/deletion automation (account closure, support-request purge) described in the docs is not yet implemented.

## Open Decisions (do not resolve unilaterally — track via team discussion / a future ADR)

### Service-to-service authentication (Care Platform API ↔ Support Assistant Service)

The assistant must only ever obtain data the authenticated end user is allowed to see; the project uses Spring Security on both sides. Options to evaluate:

1. **Propagate the end user's JWT** from the frontend, through the Support Assistant Service, to the Care Platform API on every call.
   - *Pros:* simplest; reuses the Care Platform API's existing JWT validation and RBAC checks as-is, so the assistant can never read more than the user could.
   - *Cons:* couples the assistant's call lifetime to the user's token validity/expiry; the token must be forwarded carefully (never logged or persisted).
2. **Service-to-service credentials (OAuth2 client-credentials or a dedicated service JWT) plus the user's identity passed as a separate claim/header.**
   - *Pros:* decouples "is this caller really the Support Assistant Service" from "which user is it acting for"; works even for calls not directly triggered by a live user request.
   - *Cons:* more moving parts — a second credential to issue/rotate, and the Care Platform API must map the forwarded user claim to its normal authorization checks.
3. **Static internal API key / shared secret** between the two services, with user identity passed as a plain claim.
   - *Pros:* least implementation effort.
   - *Cons:* weakest model — a long-lived static secret is hard to rotate and dangerous if leaked; doesn't fit Spring Security's token-based model well; not recommended beyond throwaway prototyping.

**Recommendation:** Option 1 for the current scope — no new trust mechanism is needed, and it directly guarantees the assistant inherits the calling user's authorization. Revisit Option 2 if the assistant ever needs to call the Care Platform API outside of a live user request (e.g. a background job).

### External data source for service information enrichment

Which external data source to integrate for enriching service information beyond provider-submitted updates is still undecided. This is an open question for the team — no proposal is made here.
