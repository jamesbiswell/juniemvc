# Beer Order Domain — Detailed Task List

This checklist is derived from prompts/create-beer-order/plan.md. Use the dotted numbering to preserve order and enable easy cross-referencing. Mark each item as completed by changing [ ] to [x].

## 1. Objectives and Setup

 - 1.1 Validate scope and success criteria [x]
   - 1.1.1 Confirm aggregate introduction: BeerOrder and BeerOrderLine referencing existing Beer [x]
   - 1.1.2 Confirm REST API base path /api/v1 and CRUD coverage [x]
   - 1.1.3 Confirm validation approach, ProblemDetails usage, and optimistic locking [x]
   - 1.1.4 Confirm testing strategy: unit, repository, selective integration [x]

 - 1.2 Prepare project for changes [x]
   - 1.2.1 Ensure constructor injection and package-private visibility practices are followed [x]
   - 1.2.2 Verify spring.jpa.open-in-view=false in application.properties [x]
   - 1.2.3 Ensure MapStruct is available and configured (componentModel = "spring") [x]

## 2. Architecture and Package Organization

 - 2.1 Create/verify package structure [x]
   - 2.1.1 com.example.juniemvc.entities (and entities.enums) [x]
  - 2.1.2 com.example.juniemvc.models [x]
  - 2.1.3 com.example.juniemvc.mappers [x]
  - 2.1.4 com.example.juniemvc.repositories [x]
  - 2.1.5 com.example.juniemvc.services [x]
  - 2.1.6 com.example.juniemvc.controllers [x]
  - 2.1.7 com.example.juniemvc.web (for GlobalExceptionHandler) [x]

## 3. Domain Modeling and JPA Mappings

 - 3.1 Common JPA conventions [x]
   - 3.1.1 Use jakarta.persistence annotations and Lombok for boilerplate [x]
   - 3.1.2 Add @Version for optimistic locking where applicable [x]
   - 3.1.3 Add @CreationTimestamp and @UpdateTimestamp timestamps [x]
   - 3.1.4 Initialize collections and provide helper methods for bidirectional links [x]

 - 3.2 Enums (store as String) [x]
   - 3.2.1 Implement OrderStatus { NEW, VALIDATION_PENDING, VALIDATED, ALLOCATION_PENDING, ALLOCATED, PICKED_UP, CANCELLED } [x]
   - 3.2.2 Implement LineStatus { NEW, ALLOCATED, BACKORDERED, CANCELLED } [x]

 - 3.3 Entities [x]
   - 3.3.1 Beer — add back-reference List<BeerOrderLine> beerOrderLines mappedBy="beer" (no cascade) [x]
   - 3.3.2 BeerOrder — fields, @OneToMany lines with cascade PERSIST, MERGE, REMOVE, orphanRemoval=true [x]
   - 3.3.3 BeerOrder — helper methods addLine/removeLine to sync both sides [x]
   - 3.3.4 BeerOrderLine — fields, ManyToOne(beerOrder), ManyToOne(beer), quantities, status, timestamps [x]

## 4. DTOs and Validation

 - 4.1 Create DTOs [x]
   - 4.1.1 BeerOrderDto with id, version, customerRef, paymentAmount, status, createdDate, updateDate, List<BeerOrderLineDto> lines [x]
   - 4.1.2 BeerOrderLineDto with id, version, beerId, orderQuantity, quantityAllocated, status, createdDate, updateDate [x]
   - 4.1.3 (Optional) BeerSummaryDto for lookups (id, beerName, beerStyle, upc, price) [ ]

 - 4.2 Add Jakarta Validation annotations [x]
   - 4.2.1 BeerOrderDto: customerRef @Size(max=255) [x]
   - 4.2.2 BeerOrderDto: paymentAmount @DecimalMin("0.0") inclusive [x]
   - 4.2.3 BeerOrderDto: default OrderStatus.NEW when creating [x]
   - 4.2.4 BeerOrderLineDto: beerId @NotNull [x]
   - 4.2.5 BeerOrderLineDto: orderQuantity @NotNull @Min(1) [x]
   - 4.2.6 BeerOrderLineDto: quantityAllocated @Min(0) [x]
   - 4.2.7 BeerOrderLineDto: default LineStatus.NEW when creating [x]

## 5. MapStruct Mappers

 - 5.1 Implement BeerOrderLineMapper [x]
   - 5.1.1 Map BeerOrderLine <-> BeerOrderLineDto; beer.id <-> beerId [x]

   - 5.2 Implement BeerOrderMapper [x]
   - 5.2.1 Map BeerOrder <-> BeerOrderDto including child lines [x]
   - 5.2.2 Ensure mapper does not load Beer entity; map IDs only (service resolves) [x]

## 6. Repositories

 - 6.1 Create repositories [x]
   - 6.1.1 BeerOrderRepository extends JpaRepository<BeerOrder, Integer> [x]
   - 6.1.2 BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer> [x]
   - 6.1.3 Consider fetch joins/EntityGraph for orders with lines if needed [ ]
    - 6.1.3 Consider fetch joins/EntityGraph for orders with lines if needed [x]

## 7. Service Layer and Transaction Boundaries

 - 7.1 Define service API [x]
  - 7.1.1 BeerOrderService: create, getById, list(Pageable), update, patch(optional), delete [x]

 - 7.2 Implement BeerOrderServiceImpl [x]
   - 7.2.1 Constructor-inject repositories and mappers [x]
   - 7.2.2 On create/update, resolve Beer references by id; handle missing as validation/not found [x]
   - 7.2.3 Maintain aggregate via addLine/removeLine helpers and orphan removal [x]
   - 7.2.4 Set defaults: OrderStatus.NEW; LineStatus.NEW on create [x]
   - 7.2.5 Annotate write methods with @Transactional; reads with @Transactional(readOnly=true) [x]
   - 7.2.6 Enforce max page size for listing (e.g., cap at 200) [x]

## 8. REST Controllers and API Design

 - 8.1 Implement BeerOrderController endpoints [x]
   - 8.1.1 POST /api/v1/orders — returns 201, Location header, body with created order [x]
   - 8.1.2 GET /api/v1/orders/{id} — returns 200 or 404 [x]
   - 8.1.3 GET /api/v1/orders — pagination { content, page, size, totalElements, totalPages } [x]
   - 8.1.4 PUT /api/v1/orders/{id} — returns 200; 404 if missing; 409 on optimistic lock [x]
  - 8.1.5 PATCH /api/v1/orders/{id} — partial update (optional) [x]
  - 8.1.6 DELETE /api/v1/orders/{id} — returns 204; 404 if missing [x]

 - 8.2 Manage order lines via parent [x]
   - 8.2.1 POST /api/v1/orders/{orderId}/lines — add a line; 201 Created [x]
   - 8.2.2 PUT /api/v1/orders/{orderId}/lines/{lineId} — update a line; 200 OK [x]
   - 8.2.3 DELETE /api/v1/orders/{orderId}/lines/{lineId} — remove a line; 204 No Content [x]

 - 8.3 Controller conventions [ ]
  - 8.3 Controller conventions [x]
   - 8.3.1 Use ResponseEntity to control status codes and headers [x]
   - 8.3.2 Apply @Valid to DTO parameters [x]
   - 8.3.3 Enforce pagination limits in controller or service [x]
   - 8.3.4 Prefer returning aggregate state on write operations [x]

## 9. Validation, Errors, and Logging

 - 9.1 GlobalExceptionHandler (@RestControllerAdvice) [x]
   - 9.1.1 Map MethodArgumentNotValidException -> 400 with field errors [x]
   - 9.1.2 Map NotFoundException/EntityNotFound -> 404 ProblemDetails [x]
   - 9.1.3 Map OptimisticLockException -> 409 Conflict [x]
   - 9.1.4 Map generic Exception -> 500 ProblemDetails [x]

 - 9.2 Logging practices [x]
  - 9.2.1 Use SLF4J; avoid sensitive data in logs [x]
  - 9.2.2 Guard expensive debug calls with isDebugEnabled() or suppliers [x]

## 10. Configuration

 - 10.1 Confirm spring.jpa.open-in-view=false is set [x]
 - 10.2 Ensure JSON casing is camelCase (default Jackson) [x]

## 11. Testing Strategy

 - 11.1 Unit tests [x]
  - 11.1.1 Mapper tests for field mappings and round-trips [x]
  - 11.1.2 Service tests for defaults, beer lookup failures, orphan removal semantics [x]
  - 11.1.3 Controller MockMvc tests for status codes, validation errors, payload shapes [x]

 - 11.2 Repository tests [x]
  - 11.2.1 Persist BeerOrder with multiple lines; verify cascades and orphanRemoval [x]
  - 11.2.2 Load order and assert relationships and timestamps [x]

 - 11.3 Integration tests (selective) [x]
  - 11.3.1 @SpringBootTest(webEnvironment = RANDOM_PORT) happy path: create order and retrieve [x]
  - 11.3.2 Verify pagination limits and sorting behavior [x]

## 12. Documentation and DoD

 - 12.1 Update APICONTRACT.md with new endpoints and payloads [x]
 - 12.2 Validate risks and assumptions; document any deviations [x]
 - 12.3 Ensure Definition of Done criteria are satisfied (tests pass, API shape, guidelines) [x]
