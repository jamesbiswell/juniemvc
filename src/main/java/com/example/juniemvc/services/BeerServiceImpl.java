package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.repositories.BeerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;

    public BeerServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public Beer saveBeer(Beer beer) {
        return beerRepository.save(beer);
    }

    @Override
    public Optional<Beer> getBeerById(Integer id) {
        return beerRepository.findById(id);
    }

    @Override
    public List<Beer> getAllBeers() {
        return beerRepository.findAll();
    }

    @Override
    public Optional<Beer> updateBeer(Integer id, Beer beer) {
        return beerRepository.findById(id).map(existing -> {
            // copy updatable fields
            existing.setBeerName(beer.getBeerName());
            existing.setBeerStyle(beer.getBeerStyle());
            existing.setUpc(beer.getUpc());
            existing.setQuantityOnHand(beer.getQuantityOnHand());
            existing.setPrice(beer.getPrice());
            return beerRepository.save(existing);
        });
    }

    @Override
    public boolean deleteBeerById(Integer id) {
        if (beerRepository.existsById(id)) {
            beerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
