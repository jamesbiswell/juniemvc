Title: Introduce Beer DTOs and MapStruct Mapping

Summary
Replace direct entity exposure in the web layer with DTOs. Introduce a BeerDto record/POJO in the models package, add a MapStruct mapper in the mappers package, refactor the service to accept/return DTOs, and update the controller to use DTOs at its boundaries. Follow Spring Boot best practices (constructor injection, package-private components, validation, and REST conventions).

Scope
- Applies only to Beer-related web, service, and mapping layers in this project.
- No database schema changes are required.

Non-goals
- Do not change repository interfaces or JPA mappings of the Beer entity.
- Do not add business logic beyond model/mapping and API boundary adjustments.

Definitions
- Entity: com.example.juniemvc.entities.Beer (JPA-managed persistence model)
- DTO: Data Transfer Object used by the web/API layer (request/response)
- Mapper: MapStruct-based converter between Entity and DTO

High-level Requirements
1) Create BeerDto in package models
   - Create a new class (or record) models.BeerDto with fields matching the Beer entity’s logical data properties:
     - Integer id (nullable for create requests)
     - Integer version
     - String beerName
     - String beerStyle
     - String upc
     - Integer quantityOnHand
     - BigDecimal price
     - LocalDateTime createdDate (read-only in responses)
     - LocalDateTime updateDate (read-only in responses)
   - Use Lombok annotations: @Data (or @Getter/@Setter), @Builder, @NoArgsConstructor, @AllArgsConstructor.
   - Validation: apply basic Jakarta Bean Validation annotations to request-relevant fields (used for create/update):
     - beerName: @NotBlank
     - beerStyle: @NotBlank
     - upc: @NotBlank
     - quantityOnHand: @NotNull
     - price: @NotNull
   - Notes:
     - id, createdDate, updateDate are server-managed; clients should not set them on create.

2) Add a MapStruct mapper in package mappers
   - Create interface mappers.BeerMapper with:
     - BeerDto toDto(Beer source)
     - Beer toEntity(BeerDto source)
     - void updateEntityFromDto(BeerDto source, @MappingTarget Beer target)
   - Annotations and rules:
     - @Mapper(componentModel = "spring")
     - When converting DTO -> Entity, ignore these target fields so they’re not overwritten by client input:
       - id
       - createdDate
       - updateDate
     - For updateEntityFromDto, also ignore id, createdDate, updateDate.
   - If needed, add @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) on update method so that null DTO fields don’t overwrite existing entity values.

3) Refactor the service layer to use DTOs
   - Service interface (com.example.juniemvc.services.BeerService):
     - Change method signatures to accept/return BeerDto or Optional<BeerDto> and List<BeerDto> where applicable.
       Examples:
       - BeerDto saveBeer(BeerDto beerDto)
       - Optional<BeerDto> getBeerById(Integer id)
       - List<BeerDto> getAllBeers()
       - Optional<BeerDto> updateBeer(Integer id, BeerDto beerDto)
       - boolean deleteBeerById(Integer id)
   - Service implementation (BeerServiceImpl):
     - Use BeerMapper to convert between DTO and Entity.
     - For create (saveBeer): map DTO -> Entity, persist, then map Entity -> DTO for the response.
     - For read: map Entity -> DTO before returning.
     - For update: fetch Entity by id; if present, update fields using mapper.updateEntityFromDto (ignoring id/created/update), persist, return mapped DTO.
   - Keep constructor injection. Do not use field/setter injection.

4) Update the controller to use DTOs
   - BeerController should expose and consume BeerDto at all endpoints (no entities in controller signatures or responses):
     - POST /api/v1/beers: accepts @Valid BeerDto as request body; returns 201 Created with BeerDto and Location header.
     - GET /api/v1/beers/{id}: returns 200 with BeerDto or 404 if not found.
     - GET /api/v1/beers: returns 200 with List<BeerDto>.
     - PUT /api/v1/beers/{id}: accepts @Valid BeerDto; returns 200 with BeerDto or 404 if not found.
     - DELETE /api/v1/beers/{id}: returns 204 or 404.
   - Keep ResponseEntity usage and existing URI patterns.
   - Apply constructor injection and keep controller class package-private if possible.

5) Mapping rules and constraints
   - DTO -> Entity ignores: id, createdDate, updateDate.
   - For updates, do not change id, createdDate, updateDate.
   - The service layer owns the conversion between DTOs and entities; the controller should only interact with DTOs.

6) Project setup (build and configuration)
   - Ensure Lombok is already present (it is used by the Beer entity). If needed, verify Lombok annotation processing is enabled.
   - Add MapStruct to pom.xml if missing:
     - Dependency: org.mapstruct:mapstruct
     - Annotation processor: org.mapstruct:mapstruct-processor
     - Ensure annotation processing is enabled for both main and test sources.

7) Validation and error handling
   - Use @Valid on controller request DTO parameters.
   - Leverage default Spring Boot validation behavior to return 400 on validation errors.
   - Optionally, centralize error responses via a @RestControllerAdvice in future work (not required in this change set).

8) Testing and acceptance criteria
   - Unit tests compile and pass after refactor.
   - Controller tests should assert that:
     - Entities are no longer used in request/response bodies; BeerDto is used instead.
     - POST returns 201 and Location header.
     - GET by id returns 200 with DTO or 404.
     - GET collection returns a list of DTOs.
     - PUT returns 200 with updated DTO or 404.
     - DELETE returns 204 or 404.
   - Service tests should verify mapper usage and that ignored fields are not overridden by client input.

Implementation Notes (examples)
// Mapper
// @Mapper(componentModel = "spring")
// public interface BeerMapper {
//   @Mapping(target = "id", ignore = true)
//   @Mapping(target = "createdDate", ignore = true)
//   @Mapping(target = "updateDate", ignore = true)
//   Beer toEntity(BeerDto source);
//   BeerDto toDto(Beer source);
//   @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//   @Mapping(target = "id", ignore = true)
//   @Mapping(target = "createdDate", ignore = true)
//   @Mapping(target = "updateDate", ignore = true)
//   void updateEntityFromDto(BeerDto source, @MappingTarget Beer target);
// }

Notes on backward compatibility
- Public API contracts change: responses and requests now use DTOs. Update clients and tests accordingly.
- Internal repository and entity code remain unchanged.

Done criteria
- New file models/BeerDto exists with Lombok builder and validation annotations.
- New file mappers/BeerMapper exists with required MapStruct annotations and methods.
- Service methods use BeerDto at the boundary and map to/from entities internally.
- Controller exposes only BeerDto, adheres to REST semantics, and compiles.
- All tests are updated to pass.
