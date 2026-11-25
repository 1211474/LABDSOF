# Rede de Apoio a Cuidadores Informais

## Requirements

- Docker Engine with Docker Compose v2
- Or, for local development: JDK 21, Maven 3.9+, Node.js 20+ and PostgreSQL 16

## Run the application with Docker

```bash
docker compose up --build
```

Open <http://localhost:3000>.

Replace the path if the project is in a different directory.

Application URLs:

- Frontend: <http://localhost:3000>
- Care Platform API: <http://localhost:8080>
- Support Assistant Service: <http://localhost:8081>
- Care Platform health: <http://localhost:8080/actuator/health>
- Support Assistant health: <http://localhost:8081/actuator/health>

Stop the application:

```bash
docker compose down
```

Stop the application and remove local database data:

```bash
docker compose down -v
```

PostgreSQL credentials can be overridden with `POSTGRES_DB`, `POSTGRES_USER` and `POSTGRES_PASSWORD` environment variables.

## Run the application locally

Start PostgreSQL:

```bash
docker compose up -d db
```

Run each component in a separate terminal:

```bash
mvn -f care-platform-api/pom.xml spring-boot:run
mvn -f support-assistant-service/pom.xml spring-boot:run
cd frontend
npm ci
npm run dev
```

The frontend runs at <http://localhost:5173> and proxies `/api/v1/ai` to port 8081 and other `/api` calls to port 8080.

## Standard service search (US05)

The Care Platform API provides the caregiver's standard search and filter fallback:

```bash
curl 'http://localhost:8080/api/v1/services?type=PHARMACY&coarseLocation=Boavista'
```

Both `type` and `coarseLocation` are optional. Results are ordered by service name within
the selected filters and include the service `source`, verification state, location, status,
and `lastUpdated` timestamp. The frontend displays these trust signals and a phone contact
action for each result.

## Plain-language service navigation (US06)

The Support Assistant Service accepts a caregiver's question in plain language and translates it
into a service search over the Care Platform directory. The parser recognises Portuguese and
English service keywords, accent-insensitive locations, and open/closed intent.

```bash
curl -X POST http://localhost:8081/api/v1/ai/navigate \
  -H "Content-Type: application/json" \
  -d '{"query":"farmácias abertas perto da Boavista"}'
```

The response contains a readable `answer`, the matching `services`, a `limitations` list and an
`isFallback` flag. Only services published by verified organisations are returned; services from
unverified organisations are excluded. For example:

```json
{
  "answer": "Encontrei 1 resultado para farmácias com estado aberto perto de Boavista. Farmácia da Boavista (Boavista; estado: aberto; horário: 08:00 - 22:00; telefone: +351220000001). Confirme sempre a disponibilidade por telefone antes de se deslocar.",
  "services": [
    {
      "type": "PHARMACY",
      "coarseLocation": "Boavista",
      "currentStatus": "OPEN"
    }
  ],
  "isFallback": true,
  "limitations": [
    "A disponibilidade em tempo real não é garantida. Confirme sempre diretamente com a instituição."
  ]
}
```

The keyword answer is always available as a fallback. When a Gemini API key is configured (see
US08 below), Gemini may produce the wording instead; otherwise the endpoint runs entirely without
AI credentials. If the Care Platform API is unavailable, the endpoint returns `503` with a
`fallbackUrl` pointing to the standard service search. If the assistant is unavailable, returns an
invalid response, or takes longer than eight seconds, the frontend shows an error and links to the
standard search; it does not display a previous answer as a new result.

## Contact a service (US11)

Each service result, including results from the assistant, provides a telephone link when a contact
number is available. The link opens the device's calling application, and the notice beside it
reminds caregivers to confirm availability directly. If no number is available, no call link is shown.

## Answer grounding in verified data (US07)

Before any answer text is produced, the navigator runs a grounding step over the application
records:

1. `VerifiedServiceGrounding` reads matching records from the Care Platform API and keeps only
   records from verified organisations with a recognised service type. Unsupported service types
   and unverified organisations are dropped.
2. Each remaining record is projected onto `GroundedService`, an explicit allowlist of fields:
   `id`, `name`, `type`, `coarseLocation`, `operatingHours`, `currentStatus`, `contactPhone`,
   `verified`, `lastUpdated` and `source`. Internal fields such as `organisationId` never leave
   the grounding layer.
3. The readable answer and the returned `services` are built only from those projected records.
   Unknown statuses are labelled `desconhecido`; no availability is claimed beyond the stored
   `currentStatus`, and the answer always asks the caregiver to confirm by phone.

Every response includes `sourceContext` with the provenance, retrieval time, allowlisted fields
and number of grounded records:

```json
{
  "sourceContext": {
    "source": "Diretório de serviços verificado da plataforma",
    "retrievedAt": "2026-11-17T10:00:00Z",
    "allowedFields": ["id", "name", "type", "coarseLocation", "operatingHours", "currentStatus", "contactPhone", "verified", "lastUpdated", "source"],
    "recordCount": 1
  }
}
```

The UI shows this source line and each result's last-update date.

## AI guardrails and Gemini integration (US08)

The Support Assistant Service now has both an **input guardrail** and an **output guardrail** around
the model call. Medical diagnoses, prescriptions, treatment advice and emergencies are refused
before any directory lookup or model request is made:

```bash
curl -X POST http://localhost:8081/api/v1/ai/navigate \
  -H "Content-Type: application/json" \
  -d '{"query":"tenho dor no peito, preciso de uma ambulância"}'
```

The response has no service results and contains a `refusal` object with the category and the
human contacts to call (`112` and SNS 24 `808 24 24 24`). The UI renders those as `tel:` links and
keeps the same source context and limitation notices as a normal answer.

For other questions, Gemini is used only when a key is present:

| Variable | Default | Purpose |
| --- | --- | --- |
| `LLM_ENABLED` | `true` | Master switch for model calls. |
| `LLM_API_KEY` | empty | Google AI Studio API key. Empty key disables Gemini. |
| `LLM_MODEL` | `gemini-2.5-flash` | Model name passed to `generateContent`. |
| `LLM_BASE_URL` | `https://generativelanguage.googleapis.com` | API origin. |
| `LLM_TIMEOUT_MS` | `5000` | Read timeout for the provider call. |

Put the key in `LLM_API_KEY` (for example in a `.env` file used by `docker compose`) — it is sent in
the `x-goog-api-key` header and is never logged or returned to the frontend.

The model receives only public, operational service facts: name, type, coarse location, opening
hours, stored status and last-updated timestamp. Service IDs, telephone numbers, care-profile
data and any other personal fields never leave the system boundary. The generated text is then
validated: it must mention a grounded service, must not contain a telephone number or medical
advice, and must not invent its own source line. If validation fails, the provider is unavailable,
or no key is configured, the deterministic grounded answer is returned with `isFallback: true`.
Every response still includes `sourceContext`, `limitations` and a Call/Contact action.

### Secrets and local API key setup

Nothing secret is committed. `.env` is gitignored; `.env.example` is the template. To use Gemini
locally:

1. Create an API key at <https://aistudio.google.com/apikey> (Google AI Studio).
2. Edit the local `.env` file and set `LLM_API_KEY=AIza...`.
3. Restart the assistant: `docker compose up -d --build` (or
   `docker compose restart support-assistant-service` if the container already exists).
4. Check the grounded answer with the `/api/v1/ai/navigate` curl above. `isFallback: false` means
   Gemini produced the wording; `true` means the deterministic fallback was used.

Leaving `LLM_API_KEY` empty is fine: the assistant then serves only grounded deterministic answers,
which is what CI does. For GitHub, store `LLM_API_KEY` under **Settings > Secrets and variables >
Actions** (or in the `staging`/`production` environments) if a deployed instance should use
Gemini. Do the same for `POSTGRES_PASSWORD` and any other deployment credential; the passwords in
`docker-compose.yml` and `.env.example` are local-development defaults only. If a key is ever
exposed, revoke and rotate it in Google AI Studio immediately.

## Run the tests

## GitHub environments and Pages

The repository includes CI for `main` and `staging`, plus a GitHub Pages workflow that builds
the frontend for whichever branch is pushed.

Configure these repository or environment variables under **Settings > Environments**:

| Environment | Variable | Value |
| --- | --- | --- |
| `staging` | `VITE_API_BASE_URL` | Public base URL of the staging Care Platform API |
| `staging` | `CORS_ALLOWED_ORIGINS` | GitHub Pages origin for staging |
| `production` | `VITE_API_BASE_URL` | Public base URL of the production Care Platform API |
| `production` | `CORS_ALLOWED_ORIGINS` | GitHub Pages origin for production |

Set the frontend API variable to the public HTTPS origin of the platform API, for example
`https://api-staging.example.com`. The support assistant base URL must be separate only when it
is exposed by a different public service. Currently, the frontend sends AI requests to
`VITE_API_BASE_URL/api/v1/ai/navigate`.

Before the first Pages deployment, enable **Settings > Pages > Build and deployment > GitHub
Actions**. GitHub Pages provides one site per repository, so pushes to `staging` and `main`
will deploy to the same Pages URL. The GitHub environment records which branch supplied the
deployment, but branch builds cannot have simultaneous URLs in this repository configuration.

Backend:

```bash
mvn -f care-platform-api/pom.xml verify
mvn -f support-assistant-service/pom.xml verify
```

Frontend:

```bash
cd frontend
npm ci
npm test
npm run build
```

JaCoCo reports are generated at `care-platform-api/target/site/jacoco/index.html` and `support-assistant-service/target/site/jacoco/index.html`.
