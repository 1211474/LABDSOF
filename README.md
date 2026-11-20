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

This release uses deterministic keyword interpretation rather than an external LLM, so the
endpoint is safe to run without AI credentials. The frontend contract is ready to be backed by the
bounded AI provider later. If the Care Platform API is unavailable, the endpoint returns `503`
with a `fallbackUrl` pointing to the standard service search. If the assistant is unavailable,
returns an invalid response, or takes longer than eight seconds, the frontend shows an error and
links to the standard search; it does not display a previous answer as a new result.

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

The UI shows this source line and each result's last-update date. This release still uses
deterministic keyword interpretation, so no external LLM prompt is sent; the same allowlist is the
boundary that would constrain any model provider added later.
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
