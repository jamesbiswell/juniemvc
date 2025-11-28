# Project Improvement Plan — Beer Order Domain

This plan translates the requirements in prompts/create-beer-order/requirements.md into a clear, phased implementation roadmap. It focuses on minimal, coherent increments, emphasizes constructor injection, package-private components, DTO boundaries, transaction scopes, and thorough testing.

## 1. Objectives and Success Criteria

### 1.1 Objectives
- Introduce BeerOrder (aggregate root) and BeerOrderLine domain models referencing existing Beer.
- Provide RESTful, versioned APIs under /api/v1 for CRUD and line management.
- Enforce validation, centralized exception handling, and optimistic locking.
- Implement repositories, services, mappers, and controllers aligned with Spring Boot guidelines.
- Add unit, repository, and selective integration tests.

### 1.2 Success Criteria
- Endpoints behave per requirements with appropriate status codes and payloads.
- Transactions scoped correctly; OSIV disabled.
- Validation errors returned using ProblemDetails; 404/409 handled.
- Tests for new components pass locally; pagination works for list endpoints.

## 2. Architecture and Package Organization

Use package-private visibility for Spring components where possible, constructor injection for dependencies.

- com.example.juniemvc.entities
  - Beer (existing, optional back-reference to lines)
  - BeerOrder
  - BeerOrderLine
  - enums
    - BeerStyle (if not already present to be re-used)
    - OrderStatus
    - LineStatus
- com.example.juniemvc.models
  - BeerOrderDto
  - BeerOrderLineDto
  - BeerSummaryDto (optional utility)
- com.example.juniemvc.mappers
  - BeerOrderMapper
  - BeerOrderLineMapper
- com.example.juniemvc.repositories
  - BeerOrderRepository
  - BeerOrderLineRepository
- com.example.juniemvc.services
  - BeerOrderService
  - BeerOrderServiceImpl
- com.example.juniemvc.controllers
  - BeerOrderController
- com.example.juniemvc.web (optional)
  - GlobalExceptionHandler (RestControllerAdvice)

## 3. Domain Modeling and JPA Mappings

### 3.1 Common JPA Conventions
- jakarta.persistence annotations; Lombok for boilerplate (@Getter/@Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor).
- @Version for optimistic locking.
- @CreationTimestamp, @UpdateTimestamp for timestamps.
- Collections initialized to empty to avoid NPEs; maintain bidirectional relationships via helper methods.

### 3.2 Entities
- Beer (existing)
  - Add optional back-reference: List<BeerOrderLine> beerOrderLines mappedBy="beer" (no cascade from Beer).

- BeerOrder (aggregate root)
  - Fields: id, version, customerRef, paymentAmount, status (OrderStatus), createdDate, updateDate.
  - Relationship: @OneToMany(mappedBy="beerOrder", cascade = {PERSIST, MERGE, REMOVE}, orphanRemoval = true)
  - Helpers: addLine(BeerOrderLine), removeLine(BeerOrderLine) synchronize both sides.

- BeerOrderLine
  - Fields: id, version, beerOrder (ManyToOne LAZY, not null), beer (ManyToOne LAZY, not null), orderQuantity, quantityAllocated, status (LineStatus), createdDate, updateDate.

### 3.3 Enums (stored as String)
- OrderStatus { NEW, VALIDATION_PENDING, VALIDATED, ALLOCATION_PENDING, ALLOCATED, PICKED_UP, CANCELLED }
- LineStatus { NEW, ALLOCATED, BACKORDERED, CANCELLED }
- BeerStyle from existing domain; reuse if present.

## 4. DTOs and Validation

### 4.1 DTOs (records/POJOs)
- BeerOrderDto
  - id, version, customerRef, paymentAmount, status, createdDate, updateDate, List<BeerOrderLineDto> lines.
- BeerOrderLineDto
  - id, version, beerId, orderQuantity, quantityAllocated, status, createdDate, updateDate.
- BeerSummaryDto (optional for lookups): id, beerName, beerStyle, upc, price.

### 4.2 Jakarta Validation
- BeerOrderDto
  - customerRef: optional, @Size(max = 255).
  - paymentAmount: optional, @DecimalMin("0.0") with inclusive=true.
  - status: optional on create; default NEW.
- BeerOrderLineDto
  - beerId: @NotNull.
  - orderQuantity: @NotNull, @Min(1).
  - quantityAllocated: @Min(0).
  - status: optional on create; default NEW.

## 5. MapStruct Mappers

### 5.1 BeerOrderMapper
- componentModel = "spring".
- Map BeerOrder <-> BeerOrderDto including child lines.
- Do not load Beer entities in mapper; map only IDs. Resolve in service.

### 5.2 BeerOrderLineMapper
- Map BeerOrderLine <-> BeerOrderLineDto.
- beer.id <-> beerId.

## 6. Repositories

- BeerOrderRepository extends JpaRepository<BeerOrder, Integer>.
- BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer>.
- Consider fetch joins or EntityGraph methods for order with lines if needed for read performance.

## 7. Service Layer and Transaction Boundaries

### 7.1 BeerOrderService API
- BeerOrderDto create(BeerOrderDto dto)
- BeerOrderDto getById(Integer id)
- Page<BeerOrderDto> list(Pageable pageable)
- BeerOrderDto update(Integer id, BeerOrderDto dto)  // full update
- Optional<BeerOrderDto> patch(Integer id, Map<String, Object> fields) // optional
- void delete(Integer id)

### 7.2 Responsibilities
- Resolve beer references by id when creating/updating lines; throw validation/NotFound if missing.
- Maintain aggregate: use addLine/removeLine helpers; synchronize removal/orphans.
- Set defaults on create: OrderStatus.NEW, LineStatus.NEW.
- Apply @Transactional on write methods; @Transactional(readOnly = true) on reads.

## 8. REST Controllers and API Design

Base path: /api/v1.

### 8.1 Beer Orders Endpoints
- POST /orders
  - Body: BeerOrderDto (id ignored)
  - 201 Created; Location: /api/v1/orders/{id}; body returns created order.

- GET /orders/{id}
  - 200 with BeerOrderDto; 404 if not found.

- GET /orders
  - Query: page (0), size (25, max 200), sort optional.
  - 200 with page-like response { content, page, size, totalElements, totalPages }.

- PUT /orders/{id}
  - 200 with updated resource; 404 if not found; 409 on optimistic lock conflict.

- PATCH /orders/{id} (optional)
  - Partial update with same error semantics as PUT.

- DELETE /orders/{id}
  - 204 No Content; 404 if not found.

### 8.2 Managing Order Lines via Parent
- POST /orders/{orderId}/lines — add line; 201 Created; returns updated order or line.
- PUT /orders/{orderId}/lines/{lineId} — update specific line; 200 OK.
- DELETE /orders/{orderId}/lines/{lineId} — remove line; 204 No Content.

### 8.3 Controller Conventions
- Use ResponseEntity to control status codes.
- Apply @Valid to DTO parameters; enforce max page size in controller or service.
- Prefer returning the aggregate state for write operations.

## 9. Validation, Errors, and Logging

### 9.1 Global Exception Handling
- @RestControllerAdvice GlobalExceptionHandler
  - MethodArgumentNotValidException -> 400 with field errors.
  - NotFoundException (custom) or EntityNotFound -> 404 ProblemDetails.
  - OptimisticLockException -> 409 Conflict.
  - Generic Exception -> 500 ProblemDetails.

### 9.2 Logging
- Use SLF4J; avoid sensitive data; guard expensive debug statements.

## 10. Configuration

- Ensure spring.jpa.open-in-view=false in application.properties.
- JSON casing camelCase (default Jackson). Confirm no conflicting global configuration.

## 11. Testing Strategy

### 11.1 Unit Tests
- Mappers: verify field mappings and round-trips.
- Services: business rules, defaults, beer lookup failures, orphan removal.
- Controllers: MockMvc tests for status codes, validation errors, and JSON payloads.

### 11.2 Repository Tests
- Persist BeerOrder with multiple lines; verify cascades and orphanRemoval; load and assert relations.

### 11.3 Integration Tests (selective)
- @SpringBootTest(webEnvironment = RANDOM_PORT) for one happy path: create order with lines and retrieve.
- Use Testcontainers if/when a real database is introduced; otherwise rely on embedded database setup.

## 12. Implementation Phases and Milestones

### Phase 1 — Domain and Repositories
1. Add enums (OrderStatus, LineStatus).
2. Implement BeerOrder and BeerOrderLine entities with mappings and helpers.
3. Optionally add Beer.beerOrderLines back-reference.
4. Create repositories for BeerOrder and BeerOrderLine.
5. Basic repository tests for CRUD and relationships.

Milestone: Entity schema compiles; repository tests green.

### Phase 2 — DTOs and Mappers
1. Add DTOs with validation annotations.
2. Implement MapStruct mappers; generate implementations.
3. Mapper unit tests.

Milestone: DTO-mapper round trips validated.

### Phase 3 — Services
1. Define BeerOrderService interface.
2. Implement BeerOrderServiceImpl with transactions and beer id resolution.
3. Service unit tests for create, update, delete, and error cases.

Milestone: Service tests green; business rules enforced.

### Phase 4 — Controllers and Exception Handling
1. Implement BeerOrderController endpoints.
2. Add GlobalExceptionHandler returning ProblemDetails.
3. Controller tests with MockMvc for validation, 404, 409, happy paths.

Milestone: Controller tests green; HTTP contract verified.

### Phase 5 — Integration Test and Hardening
1. Add one end-to-end test using RANDOM_PORT.
2. Verify pagination limits and sorting behavior.
3. Documentation updates (README or APICONTRACT.md section).

Milestone: Integration test green; documentation updated.

## 13. Data and Migration Considerations

- If using an in-memory DB during development, ensure schema matches entities; add Flyway/Liquibase later if needed.
- UPC uniqueness already in Beer; keep lookups by id for lines to avoid coupling to UPC.
- Decide whether to expose minimal BeerSummary endpoints later (out of current scope).

## 14. Risks, Assumptions, and Open Questions

### Risks
- N+1 issues when loading orders with lines; mitigate with fetch strategies or DTO projections.
- Optimistic lock conflicts under concurrent updates; ensure proper 409 handling.

### Assumptions
- Existing Beer entity, repository, and tests remain unchanged.
- H2 (or similar) available for tests; OSIV disabled globally.

### Open Questions
- Do we need PATCH support immediately? If not, defer to a later iteration.
- Should line management be strictly parent-scoped (recommended) or also expose a separate /order-lines resource?

## 15. Definition of Done

- All entities, repositories, services, mappers, and controllers implemented per requirements.
- GlobalExceptionHandler returns ProblemDetails for standard errors.
- Unit and repository tests pass; integration test verifies a full flow.
- API responses adhere to versioned paths and documented shapes; pagination enforced.
- Code follows constructor injection and package-private visibility where suitable.
