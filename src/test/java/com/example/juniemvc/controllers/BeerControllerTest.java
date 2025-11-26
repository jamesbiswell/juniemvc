package com.example.juniemvc.controllers;

import com.example.juniemvc.models.BeerDto;
import com.example.juniemvc.services.BeerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BeerService beerService;

    private BeerDto sampleBeer(Integer id) {
        return BeerDto.builder()
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
        BeerDto toCreate = sampleBeer(null);
        BeerDto saved = sampleBeer(1);

        given(beerService.saveBeer(any(BeerDto.class))).willReturn(saved);

        mockMvc.perform(post("/api/v1/beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/beers/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.beerName", is("Galaxy Cat")));
    }

    @Test
    void testCreateBeerValidationError() throws Exception {
        // Missing required fields (beerName, beerStyle, upc, quantityOnHand, price)
        BeerDto invalid = new BeerDto();
        String json = objectMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/beers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBeerByIdFound() throws Exception {
        BeerDto beer = sampleBeer(2);
        given(beerService.getBeerById(eq(2))).willReturn(Optional.of(beer));

        mockMvc.perform(get("/api/v1/beers/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.beerStyle", is("IPA")));
    }

    @Test
    void testGetBeerByIdNotFound() throws Exception {
        given(beerService.getBeerById(eq(99))).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/beers/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllBeers() throws Exception {
        BeerDto b1 = sampleBeer(1);
        BeerDto b2 = sampleBeer(2).toBuilder().beerName("Space Dog").build();
        given(beerService.getAllBeers()).willReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/v1/beers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].beerName", is("Space Dog")));
    }

    @Test
    void testUpdateBeerFound() throws Exception {
        BeerDto update = sampleBeer(null).toBuilder().beerName("Updated Cat").build();
        BeerDto updated = sampleBeer(5);
        updated.setBeerName("Updated Cat");

        given(beerService.updateBeer(eq(5), any(BeerDto.class))).willReturn(Optional.of(updated));

        mockMvc.perform(put("/api/v1/beers/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.beerName", is("Updated Cat")));
    }

    @Test
    void testUpdateBeerNotFound() throws Exception {
        BeerDto update = sampleBeer(null);
        given(beerService.updateBeer(eq(42), any(BeerDto.class))).willReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/beers/{id}", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBeerNoContent() throws Exception {
        given(beerService.deleteBeerById(eq(7))).willReturn(true);

        mockMvc.perform(delete("/api/v1/beers/{id}", 7))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBeerNotFound() throws Exception {
        given(beerService.deleteBeerById(eq(77))).willReturn(false);

        mockMvc.perform(delete("/api/v1/beers/{id}", 77))
                .andExpect(status().isNotFound());
    }
}
