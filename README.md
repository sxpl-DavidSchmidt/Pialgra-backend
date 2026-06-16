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

## API Endpoints
### User Management
- `GET /api/v1/users` - Returns all users.
- `POST /api/v1/users/create` - Creates a new user.

### Session Management
- `GET /api/v1/sessions/{username}` - Returns all sessions for the given username.
- `POST /api/v1/sessions` - Creates a new session.

### Category Management
- `GET /api/v1/categories` - Returns all categories.
- `GET /api/v1/categories/{username}` - Returns categories that belong to the given username.
- `POST /api/v1/categories/create` - Creates a new category.
