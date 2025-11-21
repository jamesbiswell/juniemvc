package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;

import java.util.List;
import java.util.Optional;

public interface BeerService {

    Beer create(Beer beer);

    Optional<Beer> findById(Integer id);

    List<Beer> findAll();
}
