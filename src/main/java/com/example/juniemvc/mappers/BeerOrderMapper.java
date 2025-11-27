package com.example.juniemvc.mappers;

import com.example.juniemvc.entities.BeerOrder;
import com.example.juniemvc.models.BeerOrderDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = { BeerOrderLineMapper.class })
public interface BeerOrderMapper {

    @Mapping(target = "lines", source = "beerOrderLines")
    BeerOrderDto toDto(BeerOrder entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "beerOrderLines", source = "lines")
    BeerOrder toEntity(BeerOrderDto dto);
}
