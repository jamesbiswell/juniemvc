# JunieMVC Developer Guidelines

## Overview
JunieMVC is a Spring Boot 3 RESTful API for managing beer data. It follows a standard layered Spring MVC architecture.

## Tech stack
- Java 21
- Spring Boot 3.5 (Web, Validation, Data JPA)
- H2 (runtime)
- Flyway (database migrations)
- Lombok
- MapStruct
- JUnit/Jupiter
- Build: Maven (use the wrapper)

## Project layout
- src/main/java/com/example/juniemvc
  - controllers: REST MVC controllers (HTTP layer)
  - services: business logic (interfaces + impl)
  - repositories: Spring Data JPA repositories
  - entities: JPA entities
- src/main/resources: application.properties, db migrations (if any)
- src/test/java: unit/integration tests mirroring main packages
- pom.xml: dependencies and plugins

Example tree:
```
src/
├── main/
│   ├── java/com/example/juniemvc/
│   │   ├── controllers/
│   │   ├── entities/
│   │   ├── repositories/
│   │   ├── services/
│   │   └── JuniemvcApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/juniemvc/
        ├── controllers/
        ├── repositories/
        └── services/
```

## Common commands
- Build (no tests):
  - Windows: mvnw.cmd -q -DskipTests package
  - Unix/macOS: ./mvnw -q -DskipTests package
- Run app:
  - Windows: mvnw.cmd spring-boot:run
  - Unix/macOS: ./mvnw spring-boot:run
- Run all tests:
  - Windows: mvnw.cmd test
  - Unix/macOS: ./mvnw test
- Run a single test class:
  - Windows: mvnw.cmd -Dtest=com.example.juniemvc.services.BeerServiceImplTest test
  - Unix/macOS: ./mvnw -Dtest=com.example.juniemvc.services.BeerServiceImplTest test

## Database & migrations
- The default profile uses in-memory H2 for local dev/testing.
- Flyway is included; place SQL migrations under src/main/resources/db/migration (V1__init.sql, V2__... etc.).
- On app start, Flyway will apply pending migrations automatically.
- H2 console (if enabled) is available at http://localhost:8080/h2-console

Current defaults (see application.properties):
- spring.jpa.hibernate.ddl-auto=create-drop
- spring.jpa.show-sql=true
- spring.h2.console.enabled=true

## API endpoints
The BeerController exposes CRUD operations at /api/v1/beers:
- GET /api/v1/beers — list all beers
- GET /api/v1/beers/{id} — get beer by id
- POST /api/v1/beers — create beer
- PUT /api/v1/beers/{id} — update beer
- DELETE /api/v1/beers/{id} — delete beer

## Coding practices
- Layers: controller → service → repository. Keep controllers thin; put logic in services.
- DTO/mapping: Prefer DTOs at controller boundaries. Use MapStruct for mapping; default component model is Spring.
- Validation: Use jakarta.validation annotations on DTOs/entities and @Valid in controller methods.
- Transactions: Put @Transactional on service methods that write data.
- Exceptions: Throw meaningful exceptions; map to responses with @ControllerAdvice as needed.
- Logging: Use slf4j (Lombok @Slf4j). Avoid System.out in production code.
- Tests: Write unit tests for services and repositories; use @DataJpaTest for repo tests and @SpringBootTest only when needed.

## Conventions
- Package by layer as in current structure.
- Use constructor injection (Lombok @RequiredArgsConstructor) instead of field injection.
- Keep methods small and names descriptive. Prefer Optional and null-safety.

## Troubleshooting
- Java version errors: ensure java -version shows 21.
- Port in use: change server.port in application.properties or free the port.
- Stale state: run clean (mvnw clean) and rebuild.
- Permission denied on Unix: chmod +x mvnw.

## Where to start
- Read HELP.md and browse tests under src/test/java for examples (e.g., Beer* tests).
- Add new features by introducing or extending controller/service/repository and their tests.