# Task List for Adding DTOs to the Beer API

## 1. Create DTO Classes
- [ ] 1.1. Create a new package `guru.springframework.juniemvc.models`
- [ ] 1.2. Create a `BeerDto` class with the following:
  - [ ] 1.2.1. Add properties (id, version, beerName, beerStyle, upc, quantityOnHand, price, createdDate, updateDate)
  - [ ] 1.2.2. Add Lombok annotations (@Data, @NoArgsConstructor, @AllArgsConstructor, @Builder)
  - [ ] 1.2.3. Add Jakarta Validation annotations:
    - [ ] 1.2.3.1. @NotBlank for beerName, beerStyle, and upc
    - [ ] 1.2.3.2. @NotNull for quantityOnHand and price
    - [ ] 1.2.3.3. @PositiveOrZero for quantityOnHand
    - [ ] 1.2.3.4. @Positive for price
  - [ ] 1.2.4. Add appropriate validation error messages

## 2. Implement MapStruct Mapper
- [ ] 2.1. Add MapStruct dependencies to pom.xml:
  - [ ] 2.1.1. Add mapstruct dependency
  - [ ] 2.1.2. Add mapstruct-processor dependency
  - [ ] 2.1.3. Configure annotation processor path in maven-compiler-plugin
- [ ] 2.2. Create a new package `guru.springframework.juniemvc.mappers`
- [ ] 2.3. Create a `BeerMapper` interface with:
  - [ ] 2.3.1. Add @Mapper annotation
  - [ ] 2.3.2. Add method to convert from Beer entity to BeerDto
  - [ ] 2.3.3. Add method to convert from BeerDto to Beer entity
  - [ ] 2.3.4. Add @Mapping annotations to ignore id, createdDate, and updateDate fields

## 3. Update Service Layer
- [ ] 3.1. Modify the `BeerService` interface:
  - [ ] 3.1.1. Update getAllBeers() to return List<BeerDto>
  - [ ] 3.1.2. Update getBeerById() to return Optional<BeerDto>
  - [ ] 3.1.3. Update saveBeer() to accept and return BeerDto
  - [ ] 3.1.4. Keep deleteBeerById() unchanged
- [ ] 3.2. Update `BeerServiceImpl` class:
  - [ ] 3.2.1. Add BeerMapper as a dependency
  - [ ] 3.2.2. Update constructor to inject BeerMapper
  - [ ] 3.2.3. Update getAllBeers() to convert entities to DTOs
  - [ ] 3.2.4. Update getBeerById() to convert entity to DTO
  - [ ] 3.2.5. Update saveBeer() to convert between DTO and entity
  - [ ] 3.2.6. Keep deleteBeerById() unchanged

## 4. Update Controller Layer
- [ ] 4.1. Modify `BeerController`:
  - [ ] 4.1.1. Update getAllBeers() to return List<BeerDto>
  - [ ] 4.1.2. Update getBeerById() to use BeerDto
  - [ ] 4.1.3. Update createBeer() to accept and return BeerDto
  - [ ] 4.1.4. Add @Valid annotation to request bodies
  - [ ] 4.1.5. Update updateBeer() to use BeerDto
  - [ ] 4.1.6. Update deleteBeer() to use BeerDto for checking existence

## 5. Update Tests
- [ ] 5.1. Update `BeerControllerTest`:
  - [ ] 5.1.1. Modify test setup to use DTOs instead of entities
  - [ ] 5.1.2. Update assertions to verify DTO properties
  - [ ] 5.1.3. Add tests for validation errors
  - [ ] 5.1.4. Ensure all tests pass with the new implementation
- [ ] 5.2. Update `BeerServiceImplTest`:
  - [ ] 5.2.1. Modify test setup to use DTOs
  - [ ] 5.2.2. Mock the BeerMapper
  - [ ] 5.2.3. Update assertions to verify DTO properties
  - [ ] 5.2.4. Ensure all tests pass with the new implementation

## 6. Additional Tasks
- [ ] 6.1. Verify all functionality works as expected
- [ ] 6.2. Ensure proper error handling for validation errors
- [ ] 6.3. Review code for any missed conversion points
- [ ] 6.4. Update documentation if necessary