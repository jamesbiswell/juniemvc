package com.example.juniemvc.mappers;

import com.example.juniemvc.entities.BeerOrderLine;
import com.example.juniemvc.models.BeerOrderLineDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BeerOrderLineMapper {

    @Mapping(target = "beerId", source = "beer.id")
    BeerOrderLineDto toDto(BeerOrderLine entity);

    // Do not load entity from DB here; create reference by id; service may validate
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "beer.id", source = "beerId")
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine toEntity(BeerOrderLineDto dto);
}
