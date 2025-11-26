package com.example.juniemvc.mappers;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.models.BeerDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper between Beer entity and BeerDto.
 */
@Mapper(componentModel = "spring")
public interface BeerMapper {

    BeerDto toDto(Beer source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Beer toEntity(BeerDto source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    void updateEntityFromDto(BeerDto source, @MappingTarget Beer target);
}
