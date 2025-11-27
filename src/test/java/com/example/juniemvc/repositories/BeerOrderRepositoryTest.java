package com.example.juniemvc.repositories;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.entities.BeerOrder;
import com.example.juniemvc.entities.BeerOrderLine;
import com.example.juniemvc.entities.enums.LineStatus;
import com.example.juniemvc.entities.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Test
    void persist_order_with_line_and_verify_cascade_and_timestamps() {
        // given a Beer
        Beer beer = beerRepository.save(Beer.builder()
                .beerName("Test Lager")
                .beerStyle("LAGER")
                .upc("UPC-123")
                .quantityOnHand(100)
                .price(new BigDecimal("9.99"))
                .build());

        // and an order with one line
        BeerOrderLine line = BeerOrderLine.builder()
                .beer(beer)
                .orderQuantity(2)
                .quantityAllocated(0)
                .status(LineStatus.NEW)
                .build();

        BeerOrder order = BeerOrder.builder()
                .customerRef("Ref-1")
                .paymentAmount(new BigDecimal("19.98"))
                .status(OrderStatus.NEW)
                .build();
        order.addLine(line);

        // when
        BeerOrder saved = beerOrderRepository.save(order);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBeerOrderLines()).hasSize(1);
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getUpdateDate()).isNotNull();

        // orphanRemoval: remove line and save
        BeerOrderLine firstLine = saved.getBeerOrderLines().get(0);
        saved.removeLine(firstLine);
        BeerOrder savedAfterRemove = beerOrderRepository.save(saved);
        assertThat(savedAfterRemove.getBeerOrderLines()).isEmpty();
    }
}
