[![Java CI with Gradle](https://github.com/sxpl-DavidSchmidt/Pialgra-backend/actions/workflows/gradle.yml/badge.svg)](https://github.com/sxpl-DavidSchmidt/Pialgra-backend/actions/workflows/gradle.yml)
[![Super-Linter](https://github.com/sxpl-DavidSchmidt/Pialgra-backend/actions/workflows/super-linter.yml/badge.svg)](https://github.com/marketplace/actions/super-linter)

# Pialgra Backend
This repository contains the server-side backend for the Pialgra software.

## Overview
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

Every endpoint except `/api/auth/register`, `/api/auth/login`, `/api/auth/logout` and
`/actuator/health` requires an authenticated session and answers `401` without one.

> CSRF protection and CORS are currently disabled for local testing. Enable both,
> and set `app.session.cookie.secure=true`, before deploying.

## API Endpoints
### Authentication
- `POST /api/auth/register` - Creates a new user. Returns `201`, or `409` if the username is taken.
- `POST /api/auth/login` - Starts a session and sets the `SESSION` cookie. Returns `401` on bad credentials.
- `POST /api/auth/logout` - Invalidates the session and expires the cookie. Returns `204`.

### /api/v1/users
- `GET` - Returns all users.
- `GET /me` - Returns the currently logged-in user.
- `GET /me/categories` - Returns the categories of the currently logged-in user.
- `GET /me/study-sessions` - Returns the study sessions of the currently logged-in user.

### /api/v1/study-sessions
- `POST` - Creates a new study session for the currently logged-in user.

### Category Management
- `POST` - Creates a new category for the currently logged-in user.
