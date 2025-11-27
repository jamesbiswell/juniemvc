package com.example.juniemvc.repositories;

import com.example.juniemvc.entities.BeerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, Integer> {

    @EntityGraph(attributePaths = {"beerOrderLines", "beerOrderLines.beer"})
    Optional<BeerOrder> findWithBeerOrderLinesById(Integer id);
}
