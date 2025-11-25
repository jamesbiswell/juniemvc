package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BeerServiceImplTest {

    @Mock
    BeerRepository beerRepository;

    @InjectMocks
    BeerServiceImpl beerService;

    Beer sample(Integer id) {
        return Beer.builder()
                .id(id)
                .beerName("Galaxy Cat")
                .beerStyle("IPA")
                .upc("12345")
                .quantityOnHand(12)
                .price(new BigDecimal("9.99"))
                .build();
    }

    @BeforeEach
    void setUp() {
        // no-op
    }

    @Test
    void saveBeer() {
        Beer toSave = sample(null);
        Beer saved = sample(1);
        given(beerRepository.save(any(Beer.class))).willReturn(saved);

        Beer result = beerService.saveBeer(toSave);
        assertThat(result.getId()).isEqualTo(1);
        verify(beerRepository).save(any(Beer.class));
    }

    @Test
    void getBeerById_found() {
        given(beerRepository.findById(eq(10))).willReturn(Optional.of(sample(10)));

        Optional<Beer> res = beerService.getBeerById(10);
        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(10);
    }

    @Test
    void getBeerById_notFound() {
        given(beerRepository.findById(eq(999))).willReturn(Optional.empty());

        Optional<Beer> res = beerService.getBeerById(999);
        assertThat(res).isNotPresent();
    }

    @Test
    void getAllBeers() {
        given(beerRepository.findAll()).willReturn(List.of(sample(1), sample(2)));

        List<Beer> list = beerService.getAllBeers();
        assertThat(list).hasSize(2);
        assertThat(list).extracting(Beer::getId).containsExactly(1, 2);
    }

    @Test
    void updateBeer_found() {
        Beer existing = sample(5);
        Beer update = sample(null);
        update.setBeerName("Updated Cat");

        given(beerRepository.findById(eq(5))).willReturn(Optional.of(existing));
        given(beerRepository.save(any(Beer.class))).willAnswer(inv -> inv.getArgument(0));

        Optional<Beer> res = beerService.updateBeer(5, update);
        assertThat(res).isPresent();
        assertThat(res.get().getBeerName()).isEqualTo("Updated Cat");
    }

    @Test
    void updateBeer_notFound() {
        Beer update = sample(null);
        given(beerRepository.findById(eq(55))).willReturn(Optional.empty());

        Optional<Beer> res = beerService.updateBeer(55, update);
        assertThat(res).isNotPresent();
    }

    @Test
    void deleteBeerById_true() {
        given(beerRepository.existsById(eq(7))).willReturn(true);

        boolean deleted = beerService.deleteBeerById(7);
        assertThat(deleted).isTrue();
        verify(beerRepository).deleteById(7);
    }

    @Test
    void deleteBeerById_false() {
        given(beerRepository.existsById(eq(77))).willReturn(false);

        boolean deleted = beerService.deleteBeerById(77);
        assertThat(deleted).isFalse();
    }
}
