package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.mappers.BeerMapper;
import com.example.juniemvc.models.BeerDto;
import com.example.juniemvc.repositories.BeerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    BeerServiceImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Override
    public BeerDto saveBeer(BeerDto beerDto) {
        Beer toSave = beerMapper.toEntity(beerDto);
        Beer saved = beerRepository.save(toSave);
        return beerMapper.toDto(saved);
    }

    @Override
    public Optional<BeerDto> getBeerById(Integer id) {
        return beerRepository.findById(id).map(beerMapper::toDto);
    }

    @Override
    public List<BeerDto> getAllBeers() {
        return beerRepository.findAll().stream()
                .map(beerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BeerDto> updateBeer(Integer id, BeerDto beerDto) {
        return beerRepository.findById(id).map(existing -> {
            beerMapper.updateEntityFromDto(beerDto, existing);
            Beer updated = beerRepository.save(existing);
            return beerMapper.toDto(updated);
        });
    }

    @Override
    public boolean deleteBeerById(Integer id) {
        if (beerRepository.existsById(id)) {
            beerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
