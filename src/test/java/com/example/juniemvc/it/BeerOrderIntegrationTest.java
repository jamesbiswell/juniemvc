package com.example.juniemvc.it;

import com.example.juniemvc.models.BeerDto;
import com.example.juniemvc.models.BeerOrderDto;
import com.example.juniemvc.models.BeerOrderLineDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void create_order_and_retrieve_and_verify_pagination_cap() {
        // create a beer
        BeerDto beerReq = BeerDto.builder()
                .beerName("Int Lager")
                .beerStyle("LAGER")
                .upc("INT-UPC-1")
                .quantityOnHand(100)
                .price(new BigDecimal("3.21"))
                .build();

        ResponseEntity<BeerDto> beerResp = restTemplate.postForEntity(url("/api/v1/beers"), beerReq, BeerDto.class);
        assertThat(beerResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer beerId = beerResp.getBody().getId();
        assertThat(beerId).isNotNull();

        // create an order with one line
        BeerOrderLineDto line = BeerOrderLineDto.builder()
                .beerId(beerId)
                .orderQuantity(2)
                .build();
        BeerOrderDto orderReq = BeerOrderDto.builder()
                .customerRef("INT-REF")
                .paymentAmount(new BigDecimal("6.42"))
                .lines(List.of(line))
                .build();

        ResponseEntity<BeerOrderDto> orderResp = restTemplate.postForEntity(url("/api/v1/orders"), orderReq, BeerOrderDto.class);
        assertThat(orderResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer orderId = orderResp.getBody().getId();
        assertThat(orderId).isNotNull();

        // retrieve the order
        ResponseEntity<BeerOrderDto> getResp = restTemplate.getForEntity(url("/api/v1/orders/" + orderId), BeerOrderDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody().getId()).isEqualTo(orderId);

        // verify pagination cap (request size 500, expect returned size <= 200)
        ResponseEntity<Map> pageResp = restTemplate.getForEntity(url("/api/v1/orders?page=0&size=500"), Map.class);
        assertThat(pageResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Object sizeReturned = pageResp.getBody().get("size");
        assertThat(Integer.valueOf(sizeReturned.toString())).isLessThanOrEqualTo(200);
    }
}
