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

Login uses a nonpersistent cookie by default and expires server-side 16 hours after
authentication. To request a persistent 30-day login, send `rememberMe: true` after
the user selects the optional checkbox explaining the fixed 30-day duration.
Omitting the flag or sending `false` uses a nonpersistent cookie. No notice-version
field is required. Both deadlines are absolute: activity does not extend them.
The session stores the choice, login timestamp and deadline
alongside the authenticated principal. This evidence is removed with the session
on logout or by expired-session cleanup. Logout also expires the browser cookie.
Authenticated sessions from before this policy are invalidated on their next request.
Retain the fixed checkbox wording in the frontend repository history
(`src/auth/rememberMeNotice.js`). If the duration or purpose changes later, review
the consent flow before deploying the change.

The public endpoints are `/api/auth/register`, `/api/auth/login`, `/api/auth/csrf`,
and `/actuator/health`. All other endpoints
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
- `GET /me` - Returns the currently logged-in user.
- `DELETE /me` - Permanently deletes the authenticated account,
  categories and study sessions, and revokes all stored login sessions.
  Requires a valid CSRF token and returns `204`. This deletes live database data;
  existing backups and operational logs follow their separate retention policies.
- `GET /me/categories` - Returns the categories of the currently logged-in user.
- `GET /me/study-sessions` - Returns the study sessions of the currently logged-in user.

### /api/v1/study-sessions
- `POST` - Creates a new study session with a category owned by the current user.
  Supply offset-aware ISO timestamps, e.g. `2026-09-18T10:00:00Z`, with end after
  start. Storage uses UTC and responses include the UTC offset. Existing naive
  timestamps are interpreted as UTC, matching the original frontend's ISO uploads.

### Category Management
- `POST` - Creates a new category for the currently logged-in user.


## Database migrations
Flyway manages the application schema; Hibernate validates it at startup.
Empty databases run `V0__initial_schema.sql` followed by all subsequent migrations.
Migrations remove obsolete image storage and the unused user role table.
Existing nonempty databases without Flyway history are baselined at version 0:
this assumes they already contain the application schema. The color migration
also preserves an existing color column and its values. Back up an
existing database before the first migration. Spring Session manages its own tables


### Account deactivation and retention

The profile UI uses `POST /api/v1/users/me/deactivate` (authenticated and CSRF-protected).
This revokes all login sessions and stores a UTC deletion deadline seven days from
deactivation. Data remains private and retained during this recovery period.
A successful password login strictly before the deadline reactivates the account;
failed logins and ordinary requests do not cancel or extend the deadline.
At or after the deadline, login is rejected even if cleanup has not run yet.

The backend scans persisted deadlines every 60 seconds (including after restart),
deleting up to 100 expired accounts per sweep, each in its own transaction.
Deletion removes study sessions, categories, the user, and login sessions.
Reactivation and cleanup lock the account row so only one can win.
The interval can be configured with `app.accounts.cleanup-delay-ms`.
Deletion can be delayed while the backend is offline or while a backlog is processed.
The existing `DELETE /api/v1/users/me` endpoint remains available for immediate erasure.

This policy only applies to explicitly deactivated accounts, not all inactive users.
Database backups and infrastructure logs need separate retention/expiry policies.
