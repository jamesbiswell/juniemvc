package com.example.juniemvc.controllers;

import com.example.juniemvc.models.BeerDto;
import com.example.juniemvc.services.BeerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller exposing CRUD operations for Beer resources.
 *
 * Contract and DTO usage
 * - Request/Response body uses BeerDto exclusively; entities are never exposed.
 * - POST and PUT accept a BeerDto. Server-managed fields (id, version, createdDate, updateDate)
 *   are ignored if provided by clients.
 * - All responses return BeerDto instances or collections thereof.
 */
@RestController
@RequestMapping("/api/v1/beers")
class BeerController {

    private final BeerService beerService;
    
    BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Creates a new Beer.
     *
     * Accepts: BeerDto (required fields: beerName, beerStyle, upc, quantityOnHand, price)
     * Returns: BeerDto for the created beer. Sets Location header to the new resource URI.
     *
     * @param beerDto     the beer payload to create (BeerDto)
     * @param uriBuilder  builder to construct the Location URI of the created resource
     * @return 201 Created with the persisted BeerDto in the body and Location header
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BeerDto> createBeer(@Valid @RequestBody BeerDto beerDto, UriComponentsBuilder uriBuilder) {
        BeerDto saved = beerService.saveBeer(beerDto);
        URI location = uriBuilder.path("/api/v1/beers/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Retrieves a Beer by its identifier.
     *
     * @param id the beer id
     * @return 200 OK with BeerDto if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable Integer id) {
        return ResponseEntity.of(beerService.getBeerById(id));
    }

    /**
     * Retrieves all Beers.
     *
     * @return 200 OK with a list of BeerDto (possibly empty)
     */
    @GetMapping
    public ResponseEntity<List<BeerDto>> getAllBeers() {
        return ResponseEntity.ok(beerService.getAllBeers());
    }

    /**
     * Updates an existing Beer identified by id using values from the request body.
     * Uses Optional semantics: if the id is not recognized, returns 404.
     *
     * Accepts: BeerDto (server-managed fields are ignored)
     * Returns: BeerDto
     *
     * @param id   the id of the beer to update
     * @param beerDto the incoming beer data with updated fields
     * @return 200 OK with the updated BeerDto if present, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeerDto> updateBeer(@PathVariable Integer id, @Valid @RequestBody BeerDto beerDto) {
        return ResponseEntity.of(beerService.updateBeer(id, beerDto));
    }

    /**
     * Deletes a Beer by id.
     *
     * @param id the id of the beer to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeer(@PathVariable Integer id) {
        boolean deleted = beerService.deleteBeerById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
