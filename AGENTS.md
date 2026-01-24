# AGENTS.md

This file contains guidelines for agentic coding assistants working in this repository.

## Project Overview

Full-stack library management system with Spring Boot backend and Vue 3 frontend.

**Tech Stack:**
- Backend: Spring Boot 3.2.0, MyBatis Plus 3.5.5, MySQL 8.0, Redis 7, JWT, SpringDoc (Swagger)
- Frontend: Vue 3, TypeScript, Vite, Tailwind CSS, Pinia, Vue Router, Axios

## Build & Test Commands

### Backend (Maven)
```bash
cd backend
# Run application
mvn spring-boot:run

# Build JAR
mvn clean package

# Run all tests
mvn test

# Run single test class
mvn test -Dtest=LibraryManagementSystemApplicationTests

# Run single test method
mvn test -Dtest=LibraryManagementSystemApplicationTests#testDatabaseConnection
```

**Default credentials:** Admin: admin / admin | User: user / user
**API Documentation:** http://localhost:8080/swagger-ui.html

### Frontend (Vite + pnpm)
```bash
cd frontend
# Install dependencies
pnpm install

# Development server
pnpm dev

# Type checking
vue-tsc --noEmit

# Build for production
pnpm build

# Preview production build
pnpm preview
```

**No test framework configured in frontend.**

## Code Style Guidelines

### Frontend (Vue 3 + TypeScript)

**Component Structure:** Use `<script setup lang="ts">` with Composition API. Define interfaces at module level. Use `ref()` for reactive state, `onMounted()` for initialization.

**Import Ordering:** Vue imports → Router/Store → API/Types → Others

**API Patterns:** Use centralized functions in `src/api/index.ts`. Auto-sets JWT token via interceptor. Handle errors with try-catch + user feedback:
```typescript
import { bookApi } from '@/api'
try {
  await bookApi.borrow(bookId)
  alert('借阅成功')
} catch (error: any) {
  alert(error.message || '借阅失败')
}
```

**State Management (Pinia):** Stores in `src/stores/` using Composition API style (`defineStore('id', () => {})`). Persist tokens in localStorage.

**Routing:** Lazy load components with `() => import('@/views/Login.vue')`. Use `meta.requiresAuth` for protected routes. Navigation guard redirects to `/login` for unauthenticated access.

**Styling:** Tailwind CSS utility classes only. No custom CSS files.

**TypeScript:** Strict mode enabled with `noUnusedLocals`, `noUnusedParameters`, `noFallthroughCasesInSwitch`. Path alias: `@/*` → `./src/*`

### Backend (Spring Boot + Java)

**Controller Layer:** `@RestController` with `@RequestMapping("/api/resource")`. Return `Result<T>` wrapper. Use SpringDoc annotations. Admin-only: `@PreAuthorize("hasRole('ADMIN')")`
```java
@RestController
@RequestMapping("/api/books")
@Tag(name = "图书管理", description = "图书相关接口")
public class BookController {
    @Autowired
    private BookService bookService;
    @GetMapping
    @Operation(summary = "获取图书列表")
    public Result<IPage<Book>> getBookList(...) {
        return Result.success(bookService.getBookList(...));
    }
}
```

**Service Layer:** Interface + Impl pattern. Extend `ServiceImpl<Mapper, Entity>`. Use `@Transactional` for writes. Throw `RuntimeException` for business errors.

**Entity Layer:** Use `@Data` (Lombok). MyBatis Plus: `@TableName`, `@TableId(type = IdType.AUTO)`, `@TableLogic`. Auto-fill timestamps: `@TableField(fill = FieldFill.INSERT/INSERT_UPDATE)`

**Mapper Layer:** Extend `BaseMapper<Entity>`. No XML for basic CRUD. Use `LambdaQueryWrapper` for dynamic queries.

**Security:** JWT in `Authorization: Bearer <token>` header. Filter extends `OncePerRequestFilter`. Roles: ADMIN, USER (prefixed with `ROLE_`)

**API Response Format:** All endpoints return `Result<T>` with code, message, data fields. Use `Result.success()`, `Result.error()`, or `Result.error(code, message)`.

**Error Handling:** Global exception handler (`GlobalExceptionHandler`) catches and formats exceptions as `Result<Void>`. Log all exceptions using SLF4J.

**Logging:** Use SLF4J with class-level logger: `private static final Logger logger = LoggerFactory.getLogger(ClassName.class);`

**Naming Conventions:** Classes: PascalCase (`BookController`). Methods/Variables: camelCase (`getBookList`). Constants: UPPER_SNAKE_CASE.

## Additional Notes

- No linter/formatter configured
- Minimal test coverage (only one test file)
- Docker: `docker/docker-compose.yml`
- Backend port: 8080 | Frontend port: 5173
- Frontend proxies `/api` to backend via Vite config
- MyBatis Plus SQL logging enabled in dev
