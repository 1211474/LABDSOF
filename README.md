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
`isFallback` flag. For example:

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
with a `fallbackUrl` pointing to the standard service search.

## Run the tests

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
