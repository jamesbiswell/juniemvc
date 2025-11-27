package com.example.juniemvc.controllers;

import com.example.juniemvc.models.BeerOrderDto;
import com.example.juniemvc.models.BeerOrderLineDto;
import com.example.juniemvc.services.BeerOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
class BeerOrderController {

    private final BeerOrderService beerOrderService;

    BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @PostMapping
    ResponseEntity<BeerOrderDto> create(@Valid @RequestBody BeerOrderDto dto) {
        BeerOrderDto created = beerOrderService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    ResponseEntity<BeerOrderDto> getById(@PathVariable Integer id) {
        BeerOrderDto dto = beerOrderService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    ResponseEntity<Map<String, Object>> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "25") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BeerOrderDto> result = beerOrderService.list(pageable);
        Map<String, Object> body = new HashMap<>();
        body.put("content", result.getContent());
        body.put("page", result.getNumber());
        body.put("size", result.getSize());
        body.put("totalElements", result.getTotalElements());
        body.put("totalPages", result.getTotalPages());
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<BeerOrderDto> update(@PathVariable Integer id, @Valid @RequestBody BeerOrderDto dto) {
        BeerOrderDto updated = beerOrderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    ResponseEntity<BeerOrderDto> patch(@PathVariable Integer id, @RequestBody BeerOrderDto dto) {
        BeerOrderDto updated = beerOrderService.patch(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        beerOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Line management via parent
    @PostMapping("/{orderId}/lines")
    ResponseEntity<BeerOrderDto> addLine(@PathVariable Integer orderId, @Valid @RequestBody BeerOrderLineDto lineDto) {
        BeerOrderDto updated = beerOrderService.addLine(orderId, lineDto);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + orderId))
                .body(updated);
    }

    @PutMapping("/{orderId}/lines/{lineId}")
    ResponseEntity<BeerOrderDto> updateLine(@PathVariable Integer orderId,
                                            @PathVariable Integer lineId,
                                            @Valid @RequestBody BeerOrderLineDto lineDto) {
        BeerOrderDto updated = beerOrderService.updateLine(orderId, lineId, lineDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderId}/lines/{lineId}")
    ResponseEntity<Void> deleteLine(@PathVariable Integer orderId, @PathVariable Integer lineId) {
        beerOrderService.deleteLine(orderId, lineId);
        return ResponseEntity.noContent().build();
    }
}
