package com.example.juniemvc.controllers;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.services.BeerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    private Beer sampleBeer(Integer id) {
        return Beer.builder()
                .id(id)
                .beerName("Galaxy Cat")
                .beerStyle("IPA")
                .upc("12345")
                .quantityOnHand(12)
                .price(new BigDecimal("9.99"))
                .build();
    }

    @Test
    void testCreateBeer() throws Exception {
        Beer toCreate = sampleBeer(null);
        Beer saved = sampleBeer(1);

        given(beerService.create(any(Beer.class))).willReturn(saved);

        mockMvc.perform(post("/api/v1/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/beer/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.beerName", is("Galaxy Cat")));
    }

    @Test
    void testGetByIdFound() throws Exception {
        Beer beer = sampleBeer(2);
        given(beerService.findById(eq(2))).willReturn(Optional.of(beer));

        mockMvc.perform(get("/api/v1/beer/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.beerStyle", is("IPA")));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        given(beerService.findById(eq(99))).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/beer/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListAll() throws Exception {
        Beer b1 = sampleBeer(1);
        Beer b2 = sampleBeer(2);
        b2.setBeerName("Space Dog");
        given(beerService.findAll()).willReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].beerName", is("Space Dog")));
    }
}
