---
name: run-tests
description: Run the unit/integration test suites for CareBnB's backend Maven modules and frontend, and locate coverage reports. Use when asked to run tests, check coverage, or confirm a change doesn't break existing tests.
---

# Running CareBnB's test suites

Each of the three components has its own independent test command — there is no single
top-level command. Run the ones affected by the change; run all three before anything that
touches shared contracts (e.g. the REST API between the two backends).

## Care Platform API

```bash
mvn -f care-platform-api/pom.xml verify
```

Coverage report: `care-platform-api/target/site/jacoco/index.html`.

## Support Assistant Service

```bash
mvn -f support-assistant-service/pom.xml verify
```

Coverage report: `support-assistant-service/target/site/jacoco/index.html`.

## Frontend

```bash
cd frontend
npm ci      # only needed after a dependency change or on a fresh checkout
npm test
npm run build
```

`npm run build` is part of the test loop here because the frontend has no separate
compile step — a broken build only surfaces there, and CI runs it right after `npm test`.

**Node version matters here.** This project targets Node 20 (`package.json`, CI). On Node
≥22, `npm test` fails both `App.test.jsx` cases with `window.localStorage.getItem is not a
function` — Node's own global `localStorage` shadows jsdom's and bypasses the polyfill guard
in `frontend/src/setupTests.js`. That's an environment artifact, not a real regression: switch
to Node 20 (e.g. `nvm use 20`) before trusting a frontend test failure.

## Coverage bar

Both backend modules already **enforce ≥80% line coverage** via JaCoCo's `check` goal bound to
the `verify` phase — `mvn verify` fails outright if a change drops coverage below that, before
you even open the report. Open the JaCoCo report above to see which lines are uncovered. The
frontend (Vitest, configured in `frontend/vite.config.js`) has no coverage reporter wired up
yet — judge frontend coverage by reading the tests next to the changed component rather than a
generated number.

## Before declaring a change done

Run the test command for every component you touched, not just the one with the new code —
e.g. a DTO shape change in `care-platform-api` can silently break
`support-assistant-service`'s integration tests even without a compile error.
