package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;

import java.util.List;
import java.util.Optional;

public interface BeerService {

    /**
     * Persists a new Beer entity.
     *
     * @param beer the beer to save
     * @return the saved Beer with generated id
     */
    Beer saveBeer(Beer beer);

    /**
     * Retrieves a Beer by its id.
     *
     * @param id the beer id
     * @return Optional containing Beer if found, otherwise empty
     */
    Optional<Beer> getBeerById(Integer id);

    /**
     * Returns all beers.
     *
     * @return list of beers, possibly empty
     */
    List<Beer> getAllBeers();

    /**
     * Updates the Beer identified by id using values from the provided Beer object.
     *
     * @param id   the id of the Beer to update
     * @param beer the new values to apply
     * @return Optional containing the updated Beer if the id exists, otherwise empty
     */
    Optional<Beer> updateBeer(Integer id, Beer beer);

    /**
     * Deletes a beer by id.
     *
     * @param id the id of the beer to delete
     * @return true if deleted, false if no beer with the given id exists
     */
    boolean deleteBeerById(Integer id);
}
