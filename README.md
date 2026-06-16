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

## Description
The backend processes requests from the frontend, interacts with the PostgreSQL database, and ensures that data is securely stored and efficiently retrieved.

## API Interaction Guide

### Base Information
- **Base path:** `/api/v1`
- **Content type:** `application/json`
- **Authentication style:** Login endpoint returns a token; clients should send it as a Bearer token in the `Authorization` header for protected routes.

Example header:

```http
Authorization: Bearer <token>
```

### Login Flow
1. Send credentials to `POST /api/v1/auth/login`.
2. Receive a JSON response containing:
   - `token` (string)
   - `expiresIn` (seconds, currently `86400` = 24h)
3. Store token client-side and attach it to future requests.

Request example:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "secret"
}
```

Response example (`200 OK`):

```json
{
  "token": "<jwt-token>",
  "expiresIn": 86400
}
```

### Endpoints

#### Auth

##### `POST /api/v1/auth/login`
- **Purpose:** Authenticate a user and return a token.
- **Request body:**

```json
{
  "username": "string",
  "password": "string"
}
```

- **Success response:** `200 OK`

```json
{
  "token": "string",
  "expiresIn": 86400
}
```

#### Users

##### `GET /api/v1/users`
- **Purpose:** Fetch all users.
- **Request body:** none
- **Success response:** `200 OK`

```json
[
  {
    "username": "string",
    "createdAt": "2026-01-01"
  }
]
```

##### `POST /api/v1/users/create`
- **Purpose:** Create a new user.
- **Request body:** `UserDto`

```json
{
  "username": "string",
  "password": "string"
}
```

- **Success response:** `201 Created`

```json
{
  "username": "string",
  "createdAt": "2026-01-01"
}
```

#### Sessions

##### `GET /api/v1/sessions/{username}`
- **Purpose:** Fetch all sessions for a specific user.
- **Path parameter:** `username`
- **Request body:** none
- **Success response:** `200 OK`

```json
[
  {
    "uuid": "00000000-0000-0000-0000-000000000000",
    "category": {
      "uuid": "00000000-0000-0000-0000-000000000000",
      "name": "string"
    },
    "startTime": "2026-01-01T10:00:00",
    "endTime": "2026-01-01T10:30:00"
  }
]
```

##### `GET /api/v1/sessions/{username}/current`
- **Purpose:** Fetch the current (active/latest) session for a user.
- **Path parameter:** `username`
- **Request body:** none
- **Success response:** `200 OK`

```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "category": {
    "uuid": "00000000-0000-0000-0000-000000000000",
    "name": "string"
  },
  "startTime": "2026-01-01T10:00:00",
  "endTime": null
}
```

##### `POST /api/v1/sessions`
- **Purpose:** Create a session.
- **Request body:** `SessionDto`
- **Success response:** `201 Created`

```json
{
  "uuid": "00000000-0000-0000-0000-000000000000",
  "category": {
    "uuid": "00000000-0000-0000-0000-000000000000",
    "name": "string"
  },
  "startTime": "2026-01-01T10:00:00",
  "endTime": "2026-01-01T10:30:00"
}
```

### Notes on Security
- The login/token flow is implemented and documented above.
- Current `SecurityConfig` allows all routes (`permitAll`), so token enforcement is not currently active for endpoint access.
- Once `.anyRequest().authenticated()` is enabled in security configuration, include the Bearer token on all protected requests.

### Error Handling
- API error responses are returned as JSON objects.
- Typical error schema:

```json
{
  "statusCode": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "startTime",
      "message": "must not be null"
    }
  ]
}
```
