package com.example.juniemvc.controllers;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.services.BeerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller exposing CRUD operations for Beer resources.
 */
@RestController
@RequestMapping("/api/v1/beers")
public class BeerController {

    private final BeerService beerService;
    
    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Creates a new Beer
     * @param beer        the beer payload to create
     * @param uriBuilder  builder to construct the Location URI of the created resource
     * @return 201 Created with the persisted Beer in the body and Location header
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Beer> createBeer(@RequestBody Beer beer, UriComponentsBuilder uriBuilder) {
        Beer saved = beerService.saveBeer(beer);
        URI location = uriBuilder.path("/api/v1/beers/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Retrieves a Beer by its identifier.
     *
     * @param id the beer id
     * @return 200 OK with Beer if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Beer> getBeerById(@PathVariable Integer id) {
        return ResponseEntity.of(beerService.getBeerById(id));
    }

    /**
     * Retrieves all Beers.
     *
     * @return 200 OK with a list of beers (possibly empty)
     */
    @GetMapping
    public ResponseEntity<List<Beer>> getAllBeers() {
        return ResponseEntity.ok(beerService.getAllBeers());
    }

    /**
     * Updates an existing Beer identified by id using values from the request body.
     * Uses Optional semantics: if the id is not recognized, returns 404.
     *
     * @param id   the id of the beer to update
     * @param beer the incoming beer data with updated fields
     * @return 200 OK with the updated Beer if present, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Beer> updateBeer(@PathVariable Integer id, @RequestBody Beer beer) {
        return ResponseEntity.of(beerService.updateBeer(id, beer));
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
