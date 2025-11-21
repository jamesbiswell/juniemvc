package com.example.juniemvc.repositories;

import com.example.juniemvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    private Beer getSampleBeer() {
        return Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle("IPA")
                .upc("12345")
                .quantityOnHand(12)
                .price(new BigDecimal("9.99"))
                .build();
    }

    @Test
    void testCreateReadUpdateDelete() {
        long startCount = beerRepository.count();

        // Create
        Beer saved = beerRepository.save(getSampleBeer());
        assertThat(saved.getId()).isNotNull();
        assertThat(beerRepository.count()).isEqualTo(startCount + 1);

        // Read
        Optional<Beer> fetchedOpt = beerRepository.findById(saved.getId());
        assertThat(fetchedOpt).isPresent();
        Beer fetched = fetchedOpt.get();
        assertThat(fetched.getBeerName()).isEqualTo("Galaxy Cat");

        // Update
        fetched.setBeerName("Super Galaxy Cat");
        Beer updated = beerRepository.save(fetched);
        assertThat(updated.getBeerName()).isEqualTo("Super Galaxy Cat");

        // Find all contains updated
        assertThat(beerRepository.findAll())
                .extracting(Beer::getId)
                .contains(updated.getId());

        // Delete
        beerRepository.deleteById(updated.getId());
        assertThat(beerRepository.findById(updated.getId())).isNotPresent();
        assertThat(beerRepository.count()).isEqualTo(startCount);
    }
}
