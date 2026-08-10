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

## Database
The schema is managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`), which only
ever adds to the schema. Renames therefore have to be applied by hand before the next
start, otherwise Hibernate creates a new empty table and the old rows are left behind.

Pending for existing databases:
```sql
ALTER TABLE sessions RENAME TO study_sessions;
```

## Authentication
Authentication is session based. Logging in returns an HttpOnly `SESSION` cookie; the
session itself is stored in the `SPRING_SESSION` table via Spring Session JDBC, so it
survives application restarts. Send the cookie with every subsequent request.

Every endpoint except `/api/register`, `/api/login`, `/api/logout` and
`/actuator/health` requires an authenticated session and answers `401` without one.

> CSRF protection and CORS are currently disabled for local testing. Enable both,
> and set `app.session.cookie.secure=true`, before deploying.

## API Endpoints
### Authentication
- `POST /api/register` - Creates a new user. Returns `201`, or `409` if the username is taken.
- `POST /api/login` - Starts a session and sets the `SESSION` cookie. Returns `401` on bad credentials.
- `POST /api/logout` - Invalidates the session and expires the cookie. Returns `204`.
- `GET /api/me` - Returns the currently logged in user.

### User Management
- `GET /api/v1/users` - Returns all users.

### Study Session Management
- `GET /api/v1/study-sessions/{username}` - Returns all study sessions for the given username.
- `POST /api/v1/study-sessions` - Creates a new study session.

### Category Management
- `GET /api/v1/categories` - Returns all categories.
- `GET /api/v1/categories/{username}` - Returns categories that belong to the given username.
- `POST /api/v1/categories/create` - Creates a new category.
