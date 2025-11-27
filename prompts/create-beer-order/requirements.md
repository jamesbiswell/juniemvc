# Beer Order Domain — Implementation Requirements (Rewritten)

This document defines the requirements to extend the application with a Beer Order domain using Spring Boot 3+, Spring Data JPA, Lombok, and MapStruct. It replaces the initial draft and provides clearer scope, API specifications, validation, and testing expectations.

## Scope
- Add Beer Order domain model consisting of BeerOrder (aggregate root) and BeerOrderLine, referencing the existing Beer entity.
- Implement full CRUD for BeerOrder and BeerOrderLine with DTOs and mappers.
- Provide RESTful, versioned APIs under /api/v1.
- Add repositories, services with clear transaction boundaries, validation, centralized error handling, and unit/integration tests.

## Non-Goals
- Payment processing, inventory allocation, or async workflows are out of scope.
- Security/authentication configuration is out of scope.

## Foundational Guidelines
- Prefer constructor injection and final fields for all mandatory dependencies.
- Keep Spring components package‑private unless wider visibility is required.
- Disable Open Session In View (OSIV) by setting spring.jpa.open-in-view=false (already recommended at app level).
- Use DTOs in controllers; do not expose entities directly.
- Follow REST best practices: versioned URLs, appropriate status codes, use ResponseEntity, consistent JSON casing (camelCase), and pagination for collections.
- Centralize exception handling with @RestControllerAdvice and return RFC 9457 ProblemDetails where feasible.

## Domain Model and JPA Mappings
### Entities and Enums
- Beer (already exists) gains an optional back-reference to BeerOrderLine. No cascade to lines is required from Beer.
- BeerOrder is the aggregate root of an order and owns BeerOrderLine children.
- BeerOrderLine links a BeerOrder to a Beer with quantities and a line status.

### Java Definitions
- Packages: com.example.juniemvc.entities for entities; com.example.juniemvc.entities.enums (or similar) for enums.
- Use jakarta.persistence annotations and Lombok.
- Use @Version for optimistic locking, @CreationTimestamp and @UpdateTimestamp for auditing timestamps.

### Required Fields
- Beer (existing): id, version, beerName, beerStyle (Enum), upc (unique), quantityOnHand, price, createdDate, updateDate, optional List<BeerOrderLine> beerOrderLines mappedBy "beer".
- BeerOrder: id, version, customerRef, paymentAmount, status (Enum), createdDate, updateDate, List<BeerOrderLine> beerOrderLines mappedBy "beerOrder" with cascade PERSIST, MERGE, REMOVE and orphanRemoval = true. Provide addLine/removeLine helpers that maintain both sides.
- BeerOrderLine: id, version, beerOrder (ManyToOne LAZY, not null), beer (ManyToOne LAZY, not null), orderQuantity, quantityAllocated, status (Enum), createdDate, updateDate.
- Enums (store as strings):
  - BeerStyle { LAGER, PILSNER, STOUT, IPA, PALE_ALE, SAISON }
  - OrderStatus { NEW, VALIDATION_PENDING, VALIDATED, ALLOCATION_PENDING, ALLOCATED, PICKED_UP, CANCELLED }
  - LineStatus { NEW, ALLOCATED, BACKORDERED, CANCELLED }

## DTOs and Mappers
### DTO Records/POJOs (package com.example.juniemvc.models)
- BeerOrderDto: Integer id, Integer version, String customerRef, BigDecimal paymentAmount, OrderStatus status, LocalDateTime createdDate, LocalDateTime updateDate, List<BeerOrderLineDto> lines.
- BeerOrderLineDto: Integer id, Integer version, Integer beerId, Integer orderQuantity, Integer quantityAllocated, LineStatus status, LocalDateTime createdDate, LocalDateTime updateDate.
- If needed for lookups, a minimal BeerSummaryDto: Integer id, String beerName, BeerStyle beerStyle, String upc, BigDecimal price.

#### Validation (Jakarta Validation)
- BeerOrderDto: customerRef optional (<= 255 chars), paymentAmount >= 0 when present; status optional on create (defaults to NEW).
- BeerOrderLineDto: beerId required; orderQuantity required and >= 1; quantityAllocated >= 0; status optional on create (defaults to NEW).

### MapStruct Mappers (package com.example.juniemvc.mappers)
- BeerOrderMapper: BeerOrder <-> BeerOrderDto; map child lines and set Beer by id via a qualified mapping or after-mapping hook in the service layer.
- BeerOrderLineMapper: BeerOrderLine <-> BeerOrderLineDto; map beer.id to beerId.
- Use componentModel = "spring". Avoid fetching entities in mappers; resolve references in services.

## Repositories (package com.example.juniemvc.repositories)
- BeerOrderRepository extends JpaRepository<BeerOrder, Integer> with default methods.
- BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer>.
- Existing BeerRepository remains as is.

## Service Layer (package com.example.juniemvc.services)
- Interfaces and implementations using constructor injection; mark service classes with @Service (package‑private visibility where possible).
### Transaction boundaries
  - @Transactional on create/update/delete methods.
  - @Transactional(readOnly = true) on read queries.
### Responsibilities
  - BeerOrderService: create, getById, list (paged), update (full), patch (partial, optional), delete.
  - Resolve beer references: when creating/updating lines, load Beer by id; if not found, fail validation.
  - Maintain aggregate invariants (synchronize add/remove line helpers).
  - Default statuses on creation: OrderStatus.NEW, LineStatus.NEW.

## REST Controllers (package com.example.juniemvc.controllers)
- Endpoints under /api/v1/orders and /api/v1/order-lines if a separate resource is needed. Prefer managing lines via the parent resource.

### Beer Orders
- POST /api/v1/orders
  - Request: BeerOrderDto (id ignored).
  - Responses: 201 Created with Location header /api/v1/orders/{id} and body; 400 on validation error.
- GET /api/v1/orders/{id}
  - 200 OK with BeerOrderDto; 404 if not found.
- GET /api/v1/orders
  - Query params: page (default 0), size (default 25, max 200), sort (optional).
  - 200 OK Page-like response: { content: [...], page, size, totalElements, totalPages }.
- PUT /api/v1/orders/{id}
  - Full update; 200 OK with updated resource; 404 if not found; 409 on optimistic lock conflict.
- PATCH /api/v1/orders/{id} (optional)
  - Partial update; same status handling as PUT.
- DELETE /api/v1/orders/{id}
  - 204 No Content if deleted; 404 if not found.

### Managing Order Lines via Parent
- POST /api/v1/orders/{orderId}/lines
  - Adds a line to order; 201 Created with updated order or the created line.
- PUT /api/v1/orders/{orderId}/lines/{lineId}
  - Updates a specific line; 200 OK with updated order or line.
- DELETE /api/v1/orders/{orderId}/lines/{lineId}
  - Removes the line; 204 No Content.

### Notes
- Prefer returning the aggregate (BeerOrderDto) for write operations to reflect server-calculated fields.
- Use ResponseEntity to set status codes explicitly.

## Validation, Errors, and Logging
- Apply Jakarta Validation on DTOs in controller method parameters with @Valid; add constraint annotations as specified.
- Global exception handling with @RestControllerAdvice:
  - MethodArgumentNotValidException -> 400 with validation details.
  - EntityNotFound or custom NotFoundException -> 404 ProblemDetails.
  - OptimisticLockException -> 409 Conflict.
  - Generic errors -> 500 ProblemDetails.
- Ensure logs use SLF4J; avoid logging sensitive data. Guard expensive debug logs.

## Configuration
- Confirm spring.jpa.open-in-view=false in application properties.
- Use snake_case or camelCase consistently in JSON (camelCase preferred). Ensure Jackson is configured accordingly (default is camelCase).

## Testing Requirements
- Unit tests:
  - Mappers: round-trip conversions and field mapping correctness.
  - Services: business logic, transaction boundaries (where practical), and error cases.
  - Controllers: slice tests using MockMvc for validation rules, status codes, and JSON payloads.
- Repository tests:
  - Basic CRUD and relationship persistence for BeerOrder and BeerOrderLine.
- Integration tests (where feasible):
  - Start application on a random port (@SpringBootTest(webEnvironment = RANDOM_PORT)).
  - Persist an order with lines and verify retrieval through the REST API.
- Aim for passing tests and reasonable coverage, aligning with the existing testing approach in the project.

## Acceptance Criteria
- Entities, enums, repositories, services, mappers, and controllers are implemented per this document.
- API endpoints behave as specified with correct status codes and payload shapes.
- Validation rejects invalid inputs; errors are returned in a consistent ProblemDetails format.
- Pagination works for list endpoints.
- Transactions are correctly applied; OSIV is disabled.
- Unit and repository tests for new components exist and pass locally.

## Appendix: Implementation Hints
- Keep collections initialized to avoid NPEs; use @Builder.Default where Lombok builders are used.
- Avoid entity loading in mappers; resolve relations in services to keep mapping pure.
- Use helper methods in BeerOrder to keep both sides of the association synchronized.
