package com.example.juniemvc.controllers;

import com.example.juniemvc.models.BeerOrderDto;
import com.example.juniemvc.models.BeerOrderLineDto;
import com.example.juniemvc.services.BeerOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BeerOrderController.class)
class BeerOrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BeerOrderService beerOrderService;

    @TestConfiguration
    static class Config {
        @Bean
        BeerOrderService beerOrderService() {
            return Mockito.mock(BeerOrderService.class);
        }
    }

    @Test
    void post_should_return_201_with_location() throws Exception {
        BeerOrderLineDto line = BeerOrderLineDto.builder()
                .beerId(1)
                .orderQuantity(2)
                .build();
        BeerOrderDto request = BeerOrderDto.builder()
                .customerRef("C1")
                .paymentAmount(new BigDecimal("12.34"))
                .lines(List.of(line))
                .build();

        BeerOrderDto response = request.toBuilder().id(99).build();
        Mockito.when(beerOrderService.create(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/orders/99"))
                .andExpect(jsonPath("$.id", is(99)));
    }

    @Test
    void post_invalid_should_return_400() throws Exception {
        // missing beerId
        BeerOrderLineDto invalidLine = BeerOrderLineDto.builder()
                .orderQuantity(1)
                .build();
        BeerOrderDto invalid = BeerOrderDto.builder().lines(List.of(invalidLine)).build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_should_return_200() throws Exception {
        BeerOrderDto response = BeerOrderDto.builder().id(5).build();
        Mockito.when(beerOrderService.getById(5)).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }
}
