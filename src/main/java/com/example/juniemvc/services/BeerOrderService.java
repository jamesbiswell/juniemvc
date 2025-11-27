package com.example.juniemvc.services;

import com.example.juniemvc.models.BeerOrderDto;
import com.example.juniemvc.models.BeerOrderLineDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BeerOrderService {
    BeerOrderDto create(BeerOrderDto dto);
    BeerOrderDto getById(Integer id);
    Page<BeerOrderDto> list(Pageable pageable);
    BeerOrderDto update(Integer id, BeerOrderDto dto);
    BeerOrderDto patch(Integer id, BeerOrderDto dto);
    void delete(Integer id);

    // Line management
    BeerOrderDto addLine(Integer orderId, BeerOrderLineDto lineDto);
    BeerOrderDto updateLine(Integer orderId, Integer lineId, BeerOrderLineDto lineDto);
    void deleteLine(Integer orderId, Integer lineId);
}
