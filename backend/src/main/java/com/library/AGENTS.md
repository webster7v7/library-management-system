# Backend Core (com.library)

**Backend entry point** for library management system. Spring Boot 3.2 + MyBatis Plus.

## Structure
```
com.library/
├── common/          # Result<T> wrapper for API responses
├── config/          # Spring Security, MyBatis Plus, Swagger, Redis, Database
├── controller/      # REST endpoints with Swagger annotations
├── dto/             # Request/response objects (LoginRequest, DashboardResponse, etc.)
├── entity/          # JPA entities with MyBatis Plus annotations
├── exception/       # Global exception handler
├── mapper/          # MyBatis Plus BaseMapper interfaces (no XML needed)
├── security/        # JWT filter, SecurityConfig, UserDetailsImpl
├── service/         # Service interfaces
└── service/impl/    # ServiceImpl<Mapper, Entity> implementations
```

## Layer Pattern
- **Controller**: `@RestController`, `@RequestMapping("/api/...")`, returns `Result<T>`
- **Service**: Interface + `ServiceImpl<Mapper, Entity>`, `@Transactional` for writes
- **Mapper**: Extends `BaseMapper<Entity>`, CRUD via inherited methods

## Key Conventions
- **MyBatis Plus**: Use `lambdaQuery()` for dynamic queries, `@TableLogic` for soft deletes
- **Logging**: `private static final Logger logger = LoggerFactory.getLogger(ClassName.class);` with detailed step-by-step logs
- **Error Handling**: Throw `RuntimeException` for business errors; `GlobalExceptionHandler` formats as `Result<Void>`
- **Admin endpoints**: `@PreAuthorize("hasRole('ADMIN')")` (roles prefixed with `ROLE_`)

## Security Flow
- JWT in `Authorization: Bearer <token>` header
- `JwtAuthenticationFilter` extends `OncePerRequestFilter`
- Redis stores token blacklist for logout

## Where to Look
| Task | Location |
|------|----------|
| Add new endpoint | Create Controller + Service interface + ServiceImpl + Entity (if needed) |
| Add new entity | Create Entity with `@TableName`, `@TableId(type = IdType.AUTO)` |
| Add validation | Add field constraints in Entity, validate in Controller |
| Configure new security rule | Modify `SecurityConfig.java` |

## Anti-Patterns
- **DO NOT** create XML mappers for basic CRUD - use inherited methods
- **NEVER** return raw entity in Controller - use DTOs or wrap in `Result<T>`
- **DO NOT** catch exceptions silently - log and return error response
