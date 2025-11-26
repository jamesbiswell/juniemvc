# Tasks: Introduce Beer DTOs and MapStruct Mapping

## 1. Preparation and Scoping
1. [ ] Review prompts/add-dtos/plan.md and confirm scope and non-goals.
2. [ ] Verify current code references to Beer entity in controller and service layers.

## 2. DTO
1. [ ] Create package com.example.juniemvc.models.
2. [ ] Add BeerDto class with Lombok: @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor.
3. [ ] Define fields: id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate.
4. [ ] Add Jakarta Validation annotations: @NotBlank on beerName, beerStyle, upc; @NotNull on quantityOnHand, price.
5. [ ] Document which fields are server-managed (id, createdDate, updateDate).

## 3. Mapper (MapStruct)
1. [ ] Add MapStruct dependencies to pom.xml: mapstruct and mapstruct-processor.
2. [ ] Ensure annotation processing is enabled for main sources.
3. [ ] Create package com.example.juniemvc.mappers.
4. [ ] Define BeerMapper interface annotated with @Mapper(componentModel = "spring").
5. [ ] Add method toDto(Beer source): BeerDto.
6. [ ] Add method toEntity(BeerDto source): Beer (ignore id, createdDate, updateDate).
7. [ ] Add method updateEntityFromDto(BeerDto source, @MappingTarget Beer target) with @BeanMapping(nullValuePropertyMappingStrategy = IGNORE) and ignore id, createdDate, updateDate.
8. [ ] Build the project to verify MapStruct generates the mapper implementation successfully.

## 4. Service Layer Refactor
1. [ ] Update BeerService interface to use DTO types:
   - [ ] BeerDto saveBeer(BeerDto beerDto)
   - [ ] Optional<BeerDto> getBeerById(Integer id)
   - [ ] List<BeerDto> getAllBeers()
   - [ ] Optional<BeerDto> updateBeer(Integer id, BeerDto beerDto)
   - [ ] boolean deleteBeerById(Integer id)
2. [ ] Refactor BeerServiceImpl:
   - [ ] Inject BeerRepository and BeerMapper via constructor (final fields).
   - [ ] Implement save: DTO -> Entity (ignore server-managed fields) -> save -> DTO.
   - [ ] Implement get by id: Entity -> DTO mapping.
   - [ ] Implement get all: map list of entities to list of DTOs.
   - [ ] Implement update: find, apply updateEntityFromDto, save, return DTO.
   - [ ] Implement delete: unchanged semantics, return boolean status.
3. [ ] Ensure visibility is appropriate (interface public, impl package-private acceptable).

## 5. Controller Refactor
1. [ ] Update BeerController to only consume/produce BeerDto.
2. [ ] POST /api/v1/beers: accept @Valid BeerDto, return 201 Created with body BeerDto and Location header.
3. [ ] GET /api/v1/beers/{id}: return 200 with BeerDto or 404 if not found.
4. [ ] GET /api/v1/beers: return 200 with List<BeerDto>.
5. [ ] PUT /api/v1/beers/{id}: accept @Valid BeerDto, return 200 with BeerDto or 404.
6. [ ] DELETE /api/v1/beers/{id}: return 204 No Content or 404.
7. [ ] Maintain constructor injection and consider package-private controller if feasible.

## 6. Validation & Error Handling
1. [ ] Ensure @Valid is applied on POST and PUT DTO parameters.
2. [ ] Verify that invalid inputs lead to 400 Bad Request via Spring’s default handling.
3. [ ] Note follow-up for standardized ProblemDetails handler (out of scope now).

## 7. Tests Update and Additions
1. [ ] Update controller tests to use BeerDto shapes for request/response.
2. [ ] Verify POST returns 201, Location header set, and response body is BeerDto.
3. [ ] Verify GET by id: 200 with DTO or 404 when not found.
4. [ ] Verify GET all: returns list of DTOs.
5. [ ] Verify PUT: 200 with updated DTO or 404 when not found.
6. [ ] Verify DELETE: 204 or 404.
7. [ ] Update service tests to reflect DTO-based API.
8. [ ] Add tests ensuring id/createdDate/updateDate are not overridden by client input on create/update.
9. [ ] Decide on using real generated mapper vs mock; update tests accordingly.

## 8. Build & Verify
1. [ ] Run mvn clean test and confirm MapStruct classes are generated and compilation passes.
2. [ ] Fix any compile errors related to mapping or refactoring.
3. [ ] Ensure all tests pass locally.

## 9. Acceptance Criteria Check
1. [ ] BeerDto exists with Lombok builder and validation annotations.
2. [ ] BeerMapper exists with required methods and ignore rules.
3. [ ] Service methods accept/return BeerDto and perform correct conversions.
4. [ ] Controller exclusively exposes BeerDto and returns correct status codes and headers.
5. [ ] All updated tests pass.

## 10. Documentation & Cleanup
1. [ ] Create an `APICONTRACT.md` internal document in the root of the project which describes the DTO-based API contract.
2. [ ] Ensure package-private visibility is applied where appropriate.
3. [ ] Remove any dead code and clean imports.
