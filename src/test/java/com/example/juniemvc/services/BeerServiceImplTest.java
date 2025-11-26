package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.mappers.BeerMapper;
import com.example.juniemvc.models.BeerDto;
import com.example.juniemvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class BeerServiceImplTest {

    @Mock
    BeerRepository beerRepository;

    BeerServiceImpl beerService;

    BeerMapper mapper = Mappers.getMapper(BeerMapper.class);

    BeerDto sampleDto(Integer id) {
        return BeerDto.builder()
                .id(id)
                .beerName("Galaxy Cat")
                .beerStyle("IPA")
                .upc("12345")
                .quantityOnHand(12)
                .price(new BigDecimal("9.99"))
                .build();
    }

    Beer sampleEntity(Integer id) {
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
        beerService = new BeerServiceImpl(beerRepository, mapper);
    }

    @Test
    void saveBeer() {
        BeerDto toCreate = sampleDto(null);
        Beer savedEntity = sampleEntity(1);
        given(beerRepository.save(any(Beer.class))).willReturn(savedEntity);

        BeerDto result = beerService.saveBeer(toCreate);
        assertThat(result.getId()).isEqualTo(1);
        verify(beerRepository).save(any(Beer.class));
    }

    @Test
    void saveBeer_ignoresServerManagedFields() {
        BeerDto toCreate = sampleDto(null);
        // Client attempts to set server-managed fields
        toCreate.setId(99);
        toCreate.setCreatedDate(LocalDateTime.now().minusDays(1));
        toCreate.setUpdateDate(LocalDateTime.now().minusDays(1));

        given(beerRepository.save(any(Beer.class))).willAnswer(inv -> {
            Beer arg = inv.getArgument(0);
            // Simulate DB assigning id and timestamps, but do not mutate the argument
            Beer persisted = new Beer();
            persisted.setId(1);
            persisted.setBeerName(arg.getBeerName());
            persisted.setBeerStyle(arg.getBeerStyle());
            persisted.setUpc(arg.getUpc());
            persisted.setQuantityOnHand(arg.getQuantityOnHand());
            persisted.setPrice(arg.getPrice());
            persisted.setCreatedDate(LocalDateTime.now());
            persisted.setUpdateDate(LocalDateTime.now());
            return persisted;
        });

        beerService.saveBeer(toCreate);

        ArgumentCaptor<Beer> captor = ArgumentCaptor.forClass(Beer.class);
        verify(beerRepository, times(1)).save(captor.capture());
        Beer savedArg = captor.getValue();
        // Ensure server-managed fields were not copied from client DTO
        assertThat(savedArg.getId()).isNull();
        assertThat(savedArg.getCreatedDate()).isNull();
        assertThat(savedArg.getUpdateDate()).isNull();
    }

    @Test
    void getBeerById_found() {
        given(beerRepository.findById(eq(10))).willReturn(Optional.of(sampleEntity(10)));

        Optional<BeerDto> res = beerService.getBeerById(10);
        assertThat(res).isPresent();
        assertThat(res.get().getId()).isEqualTo(10);
    }

    @Test
    void getBeerById_notFound() {
        given(beerRepository.findById(eq(999))).willReturn(Optional.empty());

        Optional<BeerDto> res = beerService.getBeerById(999);
        assertThat(res).isNotPresent();
    }

    @Test
    void getAllBeers() {
        given(beerRepository.findAll()).willReturn(List.of(sampleEntity(1), sampleEntity(2)));

        List<BeerDto> list = beerService.getAllBeers();
        assertThat(list).hasSize(2);
        assertThat(list).extracting(BeerDto::getId).containsExactly(1, 2);
    }

    @Test
    void updateBeer_found() {
        Beer existing = sampleEntity(5);
        existing.setCreatedDate(LocalDateTime.of(2024,1,1,0,0));
        existing.setUpdateDate(LocalDateTime.of(2024,1,2,0,0));
        BeerDto update = sampleDto(null);
        update.setBeerName("Updated Cat");
        // Client attempts to override server-managed fields
        update.setId(123);
        update.setCreatedDate(LocalDateTime.of(2030,1,1,0,0));
        update.setUpdateDate(LocalDateTime.of(2030,1,2,0,0));

        given(beerRepository.findById(eq(5))).willReturn(Optional.of(existing));
        given(beerRepository.save(any(Beer.class))).willAnswer(inv -> inv.getArgument(0));

        Optional<BeerDto> res = beerService.updateBeer(5, update);
        assertThat(res).isPresent();
        assertThat(res.get().getBeerName()).isEqualTo("Updated Cat");

        ArgumentCaptor<Beer> captor = ArgumentCaptor.forClass(Beer.class);
        verify(beerRepository).save(captor.capture());
        Beer savedArg = captor.getValue();
        // id should remain 5, created/update dates should be unchanged from existing
        assertThat(savedArg.getId()).isEqualTo(5);
        assertThat(savedArg.getCreatedDate()).isEqualTo(LocalDateTime.of(2024,1,1,0,0));
        assertThat(savedArg.getUpdateDate()).isEqualTo(LocalDateTime.of(2024,1,2,0,0));
    }

    @Test
    void updateBeer_notFound() {
        BeerDto update = sampleDto(null);
        given(beerRepository.findById(eq(55))).willReturn(Optional.empty());

        Optional<BeerDto> res = beerService.updateBeer(55, update);
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
