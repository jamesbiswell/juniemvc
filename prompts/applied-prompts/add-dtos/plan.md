# Plan: Introduce Beer DTOs and MapStruct Mapping

## 1. Objective and Rationale
Replace direct exposure of JPA entities in the web layer with dedicated DTOs and introduce a MapStruct-based mapper. This decouples API contracts from persistence models, enables validation at the edge, and aligns with the provided Spring Boot guidelines (constructor injection, package-private components, typed DTOs, validation, and REST semantics).

## 2. Scope and Non‑Goals
- In scope: Beer-related API, service, and mapping layers only.
- No DB schema changes. BeerRepository and Beer entity remain intact.
- No new business logic beyond mapping and DTO boundary changes.
- Tests will be adjusted to target DTOs instead of entities.

## 3. Current State (as of repository)
- Controllers and services currently use `com.example.juniemvc.entities.Beer` directly in method signatures and responses.
- Lombok is already present and used by entity classes.
- No MapStruct configuration exists yet.

## 4. Target Architecture Overview
- Define `models.BeerDto` as the API contract (request/response) for Beer resources.
- Implement `mappers.BeerMapper` using MapStruct to convert between `Beer` entity and `BeerDto`.
- Refactor `BeerService` API surface to accept/return `BeerDto` (or `Optional<BeerDto>` / `List<BeerDto>`), and keep entity usage internal only.
- Update `BeerController` to consume/produce `BeerDto` exclusively.

## 5. DTO Design
- Package: `com.example.juniemvc.models`
- Class: `BeerDto` (POJO or record; we will use Lombok for consistency with the project)
- Fields (aligned to logical entity fields):
  - Integer id (nullable for create)
  - Integer version
  - String beerName
  - String beerStyle
  - String upc
  - Integer quantityOnHand
  - BigDecimal price
  - LocalDateTime createdDate (server-managed)
  - LocalDateTime updateDate (server-managed)
- Lombok: `@Data` (or `@Getter`/`@Setter`), `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- Validation (Jakarta):
  - `@NotBlank` on beerName, beerStyle, upc
  - `@NotNull` on quantityOnHand, price
- Notes: id/createdDate/updateDate are server-managed; clients should not set them on create.

## 6. Mapper Design (MapStruct)
- Package: `com.example.juniemvc.mappers`
- Interface: `BeerMapper`
- Annotations: `@Mapper(componentModel = "spring")`
- Methods:
  - `BeerDto toDto(Beer source)`
  - `Beer toEntity(BeerDto source)` with target ignores: id, createdDate, updateDate
  - `void updateEntityFromDto(BeerDto source, @MappingTarget Beer target)` with ignores: id, createdDate, updateDate
- For partial updates: annotate update method with `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)` so that nulls don't overwrite existing values.

## 7. Service Layer Refactor
- Interface changes (package `com.example.juniemvc.services`):
  - `BeerDto saveBeer(BeerDto beerDto)`
  - `Optional<BeerDto> getBeerById(Integer id)`
  - `List<BeerDto> getAllBeers()`
  - `Optional<BeerDto> updateBeer(Integer id, BeerDto beerDto)`
  - `boolean deleteBeerById(Integer id)`
- Implementation (`BeerServiceImpl`):
  - Inject `BeerRepository` and `BeerMapper` via constructor (final fields).
  - Create: map DTO -> Entity (ignoring id, createdDate, updateDate), save, map back to DTO.
  - Read: map Entity -> DTO.
  - Update: find by id; if present, apply `updateEntityFromDto`, save, map to DTO.
  - Delete: unchanged semantics.
- Visibility: leave as public for interfaces; implementation can remain package-private if no external need.

## 8. Controller Refactor
- Controller: `BeerController`
- Replace entity signatures with DTOs:
  - POST `/api/v1/beers` => accepts `@Valid BeerDto`, returns `201 Created` with body `BeerDto` and `Location` header.
  - GET `/api/v1/beers/{id}` => returns `200` with `BeerDto` or `404`.
  - GET `/api/v1/beers` => returns `200` with `List<BeerDto>`.
  - PUT `/api/v1/beers/{id}` => accepts `@Valid BeerDto`, returns `200` with `BeerDto` or `404`.
  - DELETE `/api/v1/beers/{id}` => returns `204` or `404`.
- Keep constructor injection. Consider making controller class package-private if feasible.

## 9. Build and Configuration
- Add MapStruct dependencies to `pom.xml` if not present:
  - `org.mapstruct:mapstruct`
  - `org.mapstruct:mapstruct-processor` (annotationProcessor)
- Ensure annotation processing is enabled for main (and tests if necessary).
- No additional Spring configuration is required; `componentModel = "spring"` ensures the mapper is a Spring bean.

## 10. Validation & Error Handling
- Use `@Valid` on POST/PUT request DTOs.
- Rely on Spring Boot’s default 400 Bad Request for validation failures.
- Option (future work): introduce `@RestControllerAdvice` to standardize error responses (ProblemDetails per RFC 9457).

## 11. Mapping Rules and Constraints
- DTO -> Entity must ignore: id, createdDate, updateDate.
- Update should never change: id, createdDate, updateDate.
- Service is responsible for all entity/DTO conversions; controller must only see DTOs.

## 12. Test Strategy and Changes
- Update controller tests to assert DTO contracts:
  - Request/response bodies use `BeerDto` shape (no entity exposure).
  - POST returns 201 and Location header.
  - GET by id returns 200 with DTO or 404.
  - GET collection returns list of DTOs.
  - PUT returns 200 with updated DTO or 404.
  - DELETE returns 204 or 404.
- Update service tests:
  - Verify mapping behavior and that ignored fields are not overridden by client input.
  - Mock `BeerRepository` and (optionally) use real `BeerMapper` generated implementation or a mapper mock with focused assertions.

## 13. Migration Plan
1) Add `BeerDto` class and `BeerMapper` interface (non-breaking to current compile until wired).
2) Refactor `BeerService` interface and `BeerServiceImpl` implementation to use DTOs and mapper.
3) Refactor `BeerController` to use DTOs.
4) Update tests to use DTOs and adjust expectations accordingly.
5) Build and run tests; resolve compilation and behavioral issues.

## 14. Risks and Mitigations
- Risk: Breaking API change for clients connecting to the app. Mitigation: update tests and communicate contract change; if needed, version endpoint (already under `/api/v1`).
- Risk: MapStruct misconfiguration leading to missing generated mapper. Mitigation: verify pom dependencies, annotation processing, and generated sources in target.
- Risk: Validation differences causing new 400 responses. Mitigation: confirm constraints mirror current expectations; adjust tests accordingly.

## 15. Acceptance Criteria
- `models/BeerDto` added with Lombok builder and validation annotations.
- `mappers/BeerMapper` added with required MapStruct annotations and ignore rules.
- Service methods accept/return `BeerDto` and handle mapping internally.
- Controller endpoints exclusively expose `BeerDto`, maintain correct status codes and headers, and compile.
- All existing tests are updated and passing.

## 16. Work Breakdown (Actionable Tasks)
1) DTO
   - Create `src/main/java/com/example/juniemvc/models/BeerDto.java` with fields, Lombok, and validation.
2) Mapper
   - Create `src/main/java/com/example/juniemvc/mappers/BeerMapper.java` with methods and annotations.
   - Add MapStruct dependencies in `pom.xml` and verify annotation processing.
3) Service
   - Update `BeerService` method signatures to use DTO types.
   - Refactor `BeerServiceImpl` to inject `BeerMapper` and use conversions.
4) Controller
   - Update method signatures to use `BeerDto` and `@Valid`.
   - Ensure `Location` header construction on POST is preserved.
5) Tests
   - Adjust controller and service tests for DTO usage and expectations.
   - Add targeted tests for ignoring id/createdDate/updateDate on create/update.
6) Build & Verify
   - `mvn clean test` ensuring mapper generation and passing tests.

## 17. Future Enhancements (Out of Scope Now)
- Introduce `@RestControllerAdvice` with ProblemDetails responses.
- Add pagination for listing beers.
- Add MapStruct mappers for other entities if/when added.
- Introduce record-based DTOs if Lombok is to be reduced.

## 18. Conventions and Guidelines Alignment
- Constructor injection for all components.
- Prefer package-private visibility for Spring components where possible.
- Separate web layer (DTOs) from persistence (entities).
- Use validation annotations and `@Valid` in controller boundaries.
- Keep REST endpoint structure and status codes consistent.
