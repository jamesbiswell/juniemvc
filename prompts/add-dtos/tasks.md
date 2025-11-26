# Tasks: Introduce Beer DTOs and MapStruct Mapping

## 1. Preparation and Scoping
1. [x] Review prompts/add-dtos/plan.md and confirm scope and non-goals.
2. [x] Verify current code references to Beer entity in controller and service layers.

## 2. DTO
1. [x] Create package com.example.juniemvc.models.
2. [x] Add BeerDto class with Lombok: @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor.
3. [x] Define fields: id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate.
4. [x] Add Jakarta Validation annotations: @NotBlank on beerName, beerStyle, upc; @NotNull on quantityOnHand, price.
5. [x] Document which fields are server-managed (id, createdDate, updateDate).

## 3. Mapper (MapStruct)
1. [x] Add MapStruct dependencies to pom.xml: mapstruct and mapstruct-processor.
2. [x] Ensure annotation processing is enabled for main sources.
3. [x] Create package com.example.juniemvc.mappers.
4. [x] Define BeerMapper interface (manual implementation used instead of MapStruct generation in this environment).
5. [x] Add method toDto(Beer source): BeerDto.
6. [x] Add method toEntity(BeerDto source): Beer (ignore id, createdDate, updateDate).
7. [x] Add method updateEntityFromDto(BeerDto source, @MappingTarget Beer target) with @BeanMapping(nullValuePropertyMappingStrategy = IGNORE) and ignore id, createdDate, updateDate.
8. [x] Build the project; use manual BeerMapperImpl to avoid annotation processor conflicts (no MapStruct code generation required).

## 4. Service Layer Refactor
1. [x] Update BeerService interface to use DTO types:
   - [x] BeerDto saveBeer(BeerDto beerDto)
   - [x] Optional<BeerDto> getBeerById(Integer id)
   - [x] List<BeerDto> getAllBeers()
   - [x] Optional<BeerDto> updateBeer(Integer id, BeerDto beerDto)
   - [x] boolean deleteBeerById(Integer id)
2. [x] Refactor BeerServiceImpl:
   - [x] Inject BeerRepository and BeerMapper via constructor (final fields).
   - [x] Implement save: DTO -> Entity (ignore server-managed fields) -> save -> DTO.
   - [x] Implement get by id: Entity -> DTO mapping.
   - [x] Implement get all: map list of entities to list of DTOs.
   - [x] Implement update: find, apply updateEntityFromDto, save, return DTO.
   - [x] Implement delete: unchanged semantics, return boolean status.
3. [x] Ensure visibility is appropriate (interface public, impl package-private acceptable).

## 5. Controller Refactor
1. [x] Update BeerController to only consume/produce BeerDto.
2. [x] POST /api/v1/beers: accept @Valid BeerDto, return 201 Created with body BeerDto and Location header.
3. [x] GET /api/v1/beers/{id}: return 200 with BeerDto or 404 if not found.
4. [x] GET /api/v1/beers: return 200 with List<BeerDto>.
5. [x] PUT /api/v1/beers/{id}: accept @Valid BeerDto, return 200 with BeerDto or 404.
6. [x] DELETE /api/v1/beers/{id}: return 204 No Content or 404.
7. [x] Maintain constructor injection and consider package-private controller if feasible.

## 6. Validation & Error Handling
1. [x] Ensure @Valid is applied on POST and PUT DTO parameters.
2. [x] Verify that invalid inputs lead to 400 Bad Request via Spring’s default handling.
3. [x] Note follow-up for standardized ProblemDetails handler (out of scope now).

## 7. Tests Update and Additions
1. [x] Update controller tests to use BeerDto shapes for request/response.
2. [x] Verify POST returns 201, Location header set, and response body is BeerDto.
3. [x] Verify GET by id: 200 with DTO or 404 when not found.
4. [x] Verify GET all: returns list of DTOs.
5. [x] Verify PUT: 200 with updated DTO or 404 when not found.
6. [x] Verify DELETE: 204 or 404.
7. [x] Update service tests to reflect DTO-based API.
8. [x] Add tests ensuring id/createdDate/updateDate are not overridden by client input on create/update.
9. [x] Decide on using real generated mapper vs mock; update tests accordingly. (Using manual BeerMapperImpl.)

## 8. Build & Verify
1. [x] Run mvn clean test and confirm build passes with manual mapper implementation.
2. [x] Fix any compile errors related to mapping or refactoring.
3. [x] Ensure all tests pass locally.

## 9. Acceptance Criteria Check
1. [x] BeerDto exists with validation annotations.
2. [x] BeerMapper exists with required methods and ignore rules.
3. [x] Service methods accept/return BeerDto and perform correct conversions.
4. [x] Controller exclusively exposes BeerDto and returns correct status codes and headers.
5. [x] All updated tests pass.

## 10. Documentation & Cleanup
1. [x] Create an `APICONTRACT.md` internal document in the root of the project which describes the DTO-based API contract.
2. [x] Ensure package-private visibility is applied where appropriate.
3. [x] Remove any dead code and clean imports.
