package com.example.juniemvc.services;

import com.example.juniemvc.models.BeerDto;

import java.util.List;
import java.util.Optional;

public interface BeerService {

    /**
     * Persists a new Beer.
     *
     * @param beerDto the beer to save
     * @return the saved BeerDto with generated id
     */
    BeerDto saveBeer(BeerDto beerDto);

    /**
     * Retrieves a Beer by its id.
     *
     * @param id the beer id
     * @return Optional containing BeerDto if found, otherwise empty
     */
    Optional<BeerDto> getBeerById(Integer id);

    /**
     * Returns all beers.
     *
     * @return list of BeerDto, possibly empty
     */
    List<BeerDto> getAllBeers();

    /**
     * Updates the Beer identified by id using values from the provided BeerDto.
     *
     * @param id   the id of the Beer to update
     * @param beerDto the new values to apply
     * @return Optional containing the updated BeerDto if the id exists, otherwise empty
     */
    Optional<BeerDto> updateBeer(Integer id, BeerDto beerDto);

    /**
     * Deletes a beer by id.
     *
     * @param id the id of the beer to delete
     * @return true if deleted, false if no beer with the given id exists
     */
    boolean deleteBeerById(Integer id);
}
