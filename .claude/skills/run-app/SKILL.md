---
name: run-app
description: Start or stop CareBnB's three components (frontend, Care Platform API, Support Assistant Service) plus Postgres, either via Docker Compose or as local dev processes. Use when asked to run, start, launch, boot up, or stop the app.
---

# Running CareBnB

CareBnB is three independently deployable processes (`frontend`, `care-platform-api`,
`support-assistant-service`) plus a Postgres database. Pick one of the two modes below —
don't mix them (e.g. don't run `docker compose up` for the API and `npm run dev` for the
frontend against it, since the frontend dev server proxies to ports that only exist in the
same mode).

## Mode 1 — Docker Compose (default, closest to CI)

```bash
docker compose up --build        # foreground; Ctrl-C to stop
docker compose up -d --build      # detached
```

Wait for health checks before treating it as "up":

```bash
docker compose ps
curl -fsS http://localhost:8080/actuator/health   # Care Platform API
curl -fsS http://localhost:8081/actuator/health   # Support Assistant Service
curl -fsS http://localhost:3000/                   # frontend
```

Stop it:

```bash
docker compose down        # keep the Postgres volume
docker compose down -v     # also wipe local DB data — only when a clean slate is wanted
```

Logs for a failing service: `docker compose logs <service>` (service names: `db`,
`care-platform-api`, `support-assistant-service`, `frontend`).

## Mode 2 — Local dev processes (faster inner loop on one component)

Start Postgres only, then each component in its own terminal/background process:

```bash
docker compose up -d db
mvn -f care-platform-api/pom.xml spring-boot:run
mvn -f support-assistant-service/pom.xml spring-boot:run
cd frontend && npm ci && npm run dev
```

Ports in this mode: frontend dev server `5173` (proxies `/api/v1/ai` to `8081` and other
`/api` calls to `8080`), Care Platform API `8080`, Support Assistant Service `8081`.

Stop background dev processes started this way with:

```bash
pkill -f "spring-boot:run"
pkill -f "vite"
```

## Which mode to use

- Verifying a cross-component change or doing an end-to-end check → Mode 1 (matches CI's
  smoke-test job in `.github/workflows/ci.yml`).
- Iterating on one component with fast reload → Mode 2 for that component, Mode 1 (or `docker
  compose up -d db`) for its dependencies.
