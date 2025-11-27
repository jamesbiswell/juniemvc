package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.entities.enums.LineStatus;
import com.example.juniemvc.entities.enums.OrderStatus;
import com.example.juniemvc.models.BeerOrderDto;
import com.example.juniemvc.models.BeerOrderLineDto;
import com.example.juniemvc.repositories.BeerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class BeerOrderServiceImplTest {

    @Autowired
    BeerOrderService beerOrderService;

    @Autowired
    BeerRepository beerRepository;

    Integer beerId;

    @BeforeEach
    void setUp() {
        Beer beer = beerRepository.save(Beer.builder()
                .beerName("Svc Lager")
                .beerStyle("LAGER")
                .upc("SVC-1")
                .quantityOnHand(50)
                .price(new BigDecimal("5.45"))
                .build());
        beerId = beer.getId();
    }

    @Test
    void create_should_apply_default_statuses_and_resolve_beer() {
        BeerOrderLineDto line = BeerOrderLineDto.builder()
                .beerId(beerId)
                .orderQuantity(2)
                .build();
        BeerOrderDto dto = BeerOrderDto.builder()
                .customerRef("SVC-REF")
                .paymentAmount(new BigDecimal("10.90"))
                .lines(List.of(line))
                .build();

        BeerOrderDto created = beerOrderService.create(dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(created.getLines()).hasSize(1);
        assertThat(created.getLines().get(0).getStatus()).isEqualTo(LineStatus.NEW);
        assertThat(created.getLines().get(0).getBeerId()).isEqualTo(beerId);
    }

    @Test
    void create_should_fail_when_beer_missing() {
        BeerOrderLineDto line = BeerOrderLineDto.builder()
                .beerId(999_999)
                .orderQuantity(1)
                .build();
        BeerOrderDto dto = BeerOrderDto.builder()
                .lines(List.of(line))
                .build();

        assertThatThrownBy(() -> beerOrderService.create(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
