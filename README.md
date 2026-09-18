[![Java CI with Gradle](https://github.com/sxpl-DavidSchmidt/Pialgra-backend/actions/workflows/gradle.yml/badge.svg)](https://github.com/sxpl-DavidSchmidt/Pialgra-backend/actions/workflows/gradle.yml)
[![Super-Linter](https://github.com/sxpl-DavidSchmidt/Pialgra-backend/actions/workflows/super-linter.yml/badge.svg)](https://github.com/marketplace/actions/super-linter)

# Pialgra Backend
This repository contains the server-side backend for the Pialgra software.

## Overview

For public HTTPS hosting of both repositories on one machine, use
`../Pialgra-frontend/compose.production.yaml` and the frontend's `DEPLOYMENT.md`.
That stack derives CORS and secure-cookie settings from `SERVICE_DOMAIN` and
does not publish backend or database ports. This repository's `compose.yaml`
remains the local development stack; do not start both against the same database volume.
The backend is responsible for:
- Handling data access
- Managing communication between the database and the frontend application

## Technology Stack
- **Language:** Java
- **Framework:** Spring Boot
- **Database:** PostgreSQL

## Authentication
Authentication is session-based. Logging in returns an HttpOnly `SESSION` cookie; the
session itself is stored in the `SPRING_SESSION` table via Spring Session JDBC, so it
survives application restarts. Send the cookie with every subsequent request.

The public endpoints are `/api/auth/register`, `/api/auth/login`, `/api/auth/csrf`,
and `/actuator/health`. Account listing requires the ADMIN role. Other endpoints
require authentication. All POST/PUT/DELETE requests, including login and
registration, require CSRF protection: first GET `/api/auth/csrf`, retain its
session cookie, and send the returned `token` using its `headerName`. Obtain a
fresh token after login, which rotates the session ID, and after logout.

Allowed browser origins are configured with `CORS_ALLOWED_ORIGINS` (comma-separated
exact origins; local defaults are ports 5173 and 8081). Use TLS and set
`SESSION_COOKIE_SECURE=true` in production. Configure login/registration rate
limits at the public ingress. The application does not currently rate-limit them.

Copy `.env.example` to an untracked `.env` and choose a database password before
running Docker Compose. Existing database volumes retain their original password;
changing the environment variable alone does not rotate it. The PostgreSQL port
and direct backend ports are bound to loopback; expose the app through your ingress.
Never commit `.env`; rotate any real credentials that have
already been committed. Removing the file from tracking does not erase Git history.

## API Endpoints
### Authentication
- `POST /api/auth/register` - Creates a new user. Returns `201`, or `409` if the username is taken.
- `POST /api/auth/login` - Starts a session and sets the `SESSION` cookie. Returns `401` on bad credentials.
- `POST /api/auth/logout` - Invalidates the session and expires the cookie. Returns `204`.

### /api/v1/users
- `GET` - Returns all users (ADMIN only).
- `GET /me` - Returns the currently logged-in user.
- `GET /me/categories` - Returns the categories of the currently logged-in user.
- `GET /me/study-sessions` - Returns the study sessions of the currently logged-in user.

### /api/v1/study-sessions
- `POST` - Creates a new study session with a category owned by the current user.
  Supply offset-aware ISO timestamps, e.g. `2026-09-18T10:00:00Z`, with end after
  start. Storage uses UTC and responses include the UTC offset. Existing naive
  timestamps are interpreted as UTC, matching the original frontend's ISO uploads.

### Category Management
- `POST` - Creates a new category for the currently logged-in user.

### Profile pictures
- `GET /api/v1/users/me/profile-picture` returns the current image.
- `PUT /api/v1/users/me/profile-picture` accepts multipart field `image`: PNG/JPG,
  at most 10 MB, square and at most 512 × 512 pixels. The frontend crops/resizes
  larger images before upload; the backend independently validates them.
- `DELETE /api/v1/users/me/profile-picture` restores the provided default image.

Run `./gradlew test` to check authentication, authorization, validation, image
handling and persistence. The integration tests use H2; test production database
and ingress configuration separately before deployment.
