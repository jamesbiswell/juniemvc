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
