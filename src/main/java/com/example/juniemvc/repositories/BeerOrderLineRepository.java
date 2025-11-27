package com.example.juniemvc.repositories;

import com.example.juniemvc.entities.BeerOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer> {
}
