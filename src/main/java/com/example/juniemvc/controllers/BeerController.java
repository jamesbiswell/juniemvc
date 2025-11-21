package com.example.juniemvc.controllers;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.services.BeerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @PostMapping
    public ResponseEntity<Beer> create(@RequestBody Beer beer, UriComponentsBuilder uriBuilder) {
        Beer saved = beerService.create(beer);
        URI location = uriBuilder.path("/api/v1/beer/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beer> getById(@PathVariable Integer id) {
        return ResponseEntity.of(beerService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Beer>> listAll() {
        return ResponseEntity.ok(beerService.findAll());
    }
}
