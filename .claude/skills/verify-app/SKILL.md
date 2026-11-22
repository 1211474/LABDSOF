---
name: verify-app
description: End-to-end smoke-check that CareBnB's frontend, Care Platform API, and Support Assistant Service are correctly wired together after a change — starts the stack via Docker Compose and exercises health checks, service search, AI navigation, and the frontend's API proxy, mirroring the CI smoke-test job. Use before considering a nontrivial cross-component or integration change done.
---

# End-to-end verification

This mirrors the `smoke-test` job in `.github/workflows/ci.yml`. Use it when a change crosses
component boundaries (frontend ↔ API, or Care Platform API ↔ Support Assistant Service) —
unit tests alone won't catch a wiring mistake like a wrong port, path, or proxy rule.

## 1. Start the full stack and wait for health

```bash
docker compose up -d --build --wait --wait-timeout 300
```

`--wait` blocks until every service's healthcheck passes (or the command fails), so a hang
here means a real startup problem — check with `docker compose logs <service>` rather than
retrying.

## 2. Exercise the golden path

```bash
# Backend health
curl -fsS http://localhost:8080/actuator/health
curl -fsS http://localhost:8081/actuator/health

# Care Platform API: service search
curl -fsS "http://localhost:8080/api/v1/services?type=PHARMACY&coarseLocation=Boavista" -o /tmp/services.json
grep -q "PHARMACY" /tmp/services.json

# Support Assistant Service: plain-language navigation
curl -fsS -X POST http://localhost:8081/api/v1/ai/navigate \
  -H "Content-Type: application/json" \
  -d '{"query":"pharmacies near Boavista"}' \
  -o /tmp/ai.json
grep -q "PHARMACY" /tmp/ai.json

# Frontend is served and successfully proxies both backends
curl -fsS http://localhost:3000/ -o /tmp/index.html
grep -q "Rede de Apoio a Cuidadores Informais" /tmp/index.html
curl -fsS "http://localhost:3000/api/v1/services?type=PHARMACY&coarseLocation=Boavista" -o /tmp/proxied-services.json
grep -q "PHARMACY" /tmp/proxied-services.json
curl -fsS -X POST http://localhost:3000/api/v1/ai/navigate \
  -H "Content-Type: application/json" \
  -d '{"query":"pharmacies near Boavista"}' \
  -o /tmp/proxied-ai.json
grep -q "PHARMACY" /tmp/proxied-ai.json
```

For a change to a specific feature (e.g. auth, support requests, emergency contacts), add the
relevant request here too — log in via `POST /api/v1/auth/login`, then call the endpoint the
change actually touches, instead of only running the generic checks above.

## 3. On failure

```bash
docker compose logs --no-color
```

Read the failing service's log before changing anything — most failures here are a stale
build (`--build` not picking up a change) or a genuine wiring bug, not flakiness.

## 4. Tear down

```bash
docker compose down -v
```

Always tear down after verifying, even on failure, so the next run starts from a clean DB.
