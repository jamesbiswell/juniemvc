package com.example.juniemvc.mappers;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.entities.BeerOrderLine;
import com.example.juniemvc.entities.enums.LineStatus;
import com.example.juniemvc.models.BeerOrderLineDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BeerOrderLineMapperTest {

    @Autowired
    BeerOrderLineMapper mapper;

    @Test
    void toDto_and_back_should_map_beer_id_and_fields() {
        Beer beer = Beer.builder().id(42).build();
        BeerOrderLine entity = BeerOrderLine.builder()
                .id(10)
                .version(1)
                .beer(beer)
                .orderQuantity(5)
                .quantityAllocated(3)
                .status(LineStatus.NEW)
                .build();

        BeerOrderLineDto dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(10);
        assertThat(dto.getBeerId()).isEqualTo(42);
        assertThat(dto.getOrderQuantity()).isEqualTo(5);
        assertThat(dto.getQuantityAllocated()).isEqualTo(3);
        assertThat(dto.getStatus()).isEqualTo(LineStatus.NEW);

        // back to entity (beer assigned by id, beerOrder ignored)
        BeerOrderLine mappedEntity = mapper.toEntity(dto);
        assertThat(mappedEntity.getBeer()).isNotNull();
        assertThat(mappedEntity.getBeer().getId()).isEqualTo(42);
        assertThat(mappedEntity.getOrderQuantity()).isEqualTo(5);
        assertThat(mappedEntity.getQuantityAllocated()).isEqualTo(3);
        assertThat(mappedEntity.getStatus()).isEqualTo(LineStatus.NEW);
    }
}
