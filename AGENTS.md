# PROJECT KNOWLEDGE BASE

**Generated:** 2026-01-27
**Tech Stack:** Spring Boot 3.2 + Vue 3 + MySQL + Redis

## Overview
Full-stack library management system with JWT auth, role-based access (ADMIN/USER).

## Structure
```
.
├── backend/          # Spring Boot REST API (port 8080)
│   └── src/main/java/com/library/
│       ├── config/   # Security, MyBatis, Swagger, Redis
│       ├── controller/  # REST endpoints (books, auth, dashboard, etc.)
│       ├── service/  # Business logic (interface + impl pattern)
│       ├── mapper/   # MyBatis Plus data access
│       ├── entity/   # JPA entities
│       ├── dto/      # Request/response DTOs
│       └── security/ # JWT auth filter
├── frontend/         # Vue 3 + TypeScript UI (port 5173)
│   └── src/
│       ├── api/      # Centralized API functions
│       ├── views/    # Page components
│       ├── stores/   # Pinia state management
│       └── router/   # Vue Router config
└── docker/          # docker-compose.yml
```

## Where to Look
| Task | Location |
|------|----------|
| **Add backend feature** | Backend AGENTS.md (backend/src/main/java/com/library/) |
| **Add frontend page** | Frontend AGENTS.md (frontend/src/) |
| **Run backend** | `cd backend && mvn spring-boot:run` |
| **Run frontend** | `cd frontend && pnpm dev` |
| **API docs** | http://localhost:8080/swagger-ui.html |

## Commands

### Backend
```bash
cd backend
mvn spring-boot:run              # Dev server
mvn clean package               # Build JAR
mvn test                        # Run tests
```

### Frontend
```bash
cd frontend
pnpm install                    # Install deps
pnpm dev                        # Dev server (localhost:5173)
vue-tsc --noEmit               # Type check
pnpm build                      # Production build
```

## Key Patterns

**Backend (Spring Boot):**
- Controller → Service → Mapper layered architecture
- `Result<T>` wrapper for all API responses
- MyBatis Plus for CRUD (no XML needed for basic ops)
- JWT via `JwtAuthenticationFilter`

**Frontend (Vue 3):**
- Composition API with `<script setup lang="ts">`
- Centralized API functions in `api/index.ts`
- Tailwind CSS only (no custom CSS)
- Pinia for state (persisted in localStorage)

## Credentials
- **Admin:** admin / admin
- **User:** user / user

## Notes
- Frontend proxies `/api` → backend (Vite config)
- No linter/formatter configured
- Minimal test coverage (only 1 test file)
- MyBatis Plus SQL logging enabled in dev
