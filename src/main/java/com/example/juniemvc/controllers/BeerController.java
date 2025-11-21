package com.example.juniemvc.controllers;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.services.BeerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beers")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Beer> createBeer(@RequestBody Beer beer, UriComponentsBuilder uriBuilder) {
        Beer saved = beerService.saveBeer(beer);
        URI location = uriBuilder.path("/api/v1/beers/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beer> getBeerById(@PathVariable Integer id) {
        return ResponseEntity.of(beerService.getBeerById(id));
    }

    @GetMapping
    public ResponseEntity<List<Beer>> getAllBeers() {
        return ResponseEntity.ok(beerService.getAllBeers());
    }
}
