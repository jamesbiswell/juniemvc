### Beer API Contract (DTO-based)

This document describes the Beer API as exposed by the Spring MVC controller using DTOs. It reflects the current behavior verified by tests.

Base path: /api/v1/beers

- Media type: application/json
- All endpoints use BeerDto for request and response bodies.

BeerDto fields
- id: Integer (server-managed; nullable on create)
- version: Integer
- beerName: String (required, not blank)
- beerStyle: String (required, not blank)
- upc: String (required, not blank)
- quantityOnHand: Integer (required, not null)
- price: BigDecimal (required, not null)
- createdDate: LocalDateTime (server-managed; read-only)
- updateDate: LocalDateTime (server-managed; read-only)

Endpoints
1) POST /api/v1/beers
   - Request body: BeerDto (beerName, beerStyle, upc, quantityOnHand, price are required)
   - Response: 201 Created
     - Location header: /api/v1/beers/{id}
     - Body: BeerDto with generated id and server-managed timestamps
   - Validation: If required fields are missing/invalid, returns 400 Bad Request.

2) GET /api/v1/beers/{id}
   - Path variable: id (integer)
   - Response:
     - 200 OK with BeerDto when found
     - 404 Not Found when id does not exist

3) GET /api/v1/beers
   - Response: 200 OK with an array of BeerDto (possibly empty)

4) PUT /api/v1/beers/{id}
   - Request body: BeerDto (validated like POST)
   - Response:
     - 200 OK with updated BeerDto when id exists
     - 404 Not Found when id does not exist
   - Server-managed fields (id, createdDate, updateDate) are not overridden by client input.

5) DELETE /api/v1/beers/{id}
   - Response:
     - 204 No Content when deleted
     - 404 Not Found when id does not exist

Notes
- Controller, service, and mapper follow constructor injection and package-private visibility where appropriate.
- Validation errors rely on Spring Boot’s default handler, returning 400 with ProblemDetail-like body.
- Mapping between entity and DTO is handled by BeerMapper (manual implementation `BeerMapperImpl`).


### Beer Order API Contract (DTO-based)

This section describes the Beer Order API exposed by the BeerOrderController using DTOs. All endpoints are versioned under /api/v1.

Base path: /api/v1/orders

- Media type: application/json
- All endpoints use BeerOrderDto for requests/responses, and BeerOrderLineDto for line operations.

BeerOrderDto fields
- id: Integer (server-managed; ignored on create)
- version: Integer
- customerRef: String (optional, max 255)
- paymentAmount: BigDecimal (optional, >= 0)
- status: OrderStatus (defaults to NEW on create)
- createdDate: LocalDateTime (read-only)
- updateDate: LocalDateTime (read-only)
- lines: List<BeerOrderLineDto>

BeerOrderLineDto fields
- id: Integer (server-managed; for updates)
- version: Integer
- beerId: Integer (required)
- orderQuantity: Integer (required, >= 1)
- quantityAllocated: Integer (>= 0)
- status: LineStatus (defaults to NEW on create)
- createdDate, updateDate: LocalDateTime (read-only)

Endpoints
1) POST /api/v1/orders
   - Request: BeerOrderDto (lines optional; each line requires beerId and orderQuantity)
   - Response: 201 Created
     - Location: /api/v1/orders/{id}
     - Body: BeerOrderDto with generated id and timestamps
   - Errors: 400 validation errors

2) GET /api/v1/orders/{id}
   - Response: 200 OK with BeerOrderDto; 404 if not found

3) GET /api/v1/orders?page={page}&size={size}
   - Response: 200 OK with an envelope
     { content: BeerOrderDto[], page, size, totalElements, totalPages }
   - Notes: size is capped at 200 on the server side

4) PUT /api/v1/orders/{id}
   - Request: BeerOrderDto (full update). Lines provided replace existing lines.
   - Response: 200 OK with updated BeerOrderDto; 404 if not found; 409 on optimistic lock

5) PATCH /api/v1/orders/{id}
   - Request: BeerOrderDto (partial update of simple fields: customerRef, paymentAmount, status)
   - Response: 200 OK with updated BeerOrderDto; 404 if not found
   - Notes: Lines are not patched by this endpoint. Use line-specific endpoints below.

6) DELETE /api/v1/orders/{id}
   - Response: 204 No Content; 404 if not found

Managing Order Lines via Parent Resource
7) POST /api/v1/orders/{orderId}/lines
   - Request: BeerOrderLineDto (beerId, orderQuantity required)
   - Response: 201 Created with Location /api/v1/orders/{orderId} and body: updated BeerOrderDto

8) PUT /api/v1/orders/{orderId}/lines/{lineId}
   - Request: BeerOrderLineDto (fields to update)
   - Response: 200 OK with updated BeerOrderDto; 404 if order or line not found

9) DELETE /api/v1/orders/{orderId}/lines/{lineId}
   - Response: 204 No Content; 404 if order not found

Errors
- Validation errors: 400 with ProblemDetail containing field errors
- Not found: 404 ProblemDetail with message
- Optimistic lock conflict: 409 ProblemDetail
- Generic errors: 500 ProblemDetail

Notes
- Controllers and services follow constructor injection and prefer package-private visibility.
- OSIV disabled (spring.jpa.open-in-view=false). Transactions are scoped at the service layer.

Risks, Assumptions, and Deviations
- Assumption: Inventory allocation and payment workflows are out of scope; statuses are set only by CRUD operations.
- Assumption: Beer must exist prior to creating an order line; service validates beerId and throws 404 when missing.
- Risk: N+1 queries when loading orders and lines in bulk; mitigated with an EntityGraph method for single-load detail views (findWithBeerOrderLinesById).
- Deviation: PATCH endpoint intentionally excludes patching of child lines; dedicated endpoints are provided for line operations.

Definition of Done (current state)
- Entities, DTOs, mappers, repositories, services, and controllers implemented per requirements.
- Validation applied; centralized exception handling returns RFC 9457 ProblemDetails.
- Pagination implemented and server-side capped at 200.
- Unit tests (mappers, services), repository test (cascade/orphanRemoval), controller slice tests, and integration test (RANDOM_PORT) added.
- API contract updated; OSIV disabled; constructor injection and package-private visibility followed.
