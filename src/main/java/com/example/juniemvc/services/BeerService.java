package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;

import java.util.List;
import java.util.Optional;

public interface BeerService {

    Beer saveBeer(Beer beer);

    Optional<Beer> getBeerById(Integer id);

    List<Beer> getAllBeers();
}
