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
