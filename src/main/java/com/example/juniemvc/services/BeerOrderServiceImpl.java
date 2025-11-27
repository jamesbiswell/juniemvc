package com.example.juniemvc.services;

import com.example.juniemvc.entities.Beer;
import com.example.juniemvc.entities.BeerOrder;
import com.example.juniemvc.entities.BeerOrderLine;
import com.example.juniemvc.entities.enums.LineStatus;
import com.example.juniemvc.entities.enums.OrderStatus;
import com.example.juniemvc.mappers.BeerOrderLineMapper;
import com.example.juniemvc.mappers.BeerOrderMapper;
import com.example.juniemvc.models.BeerOrderDto;
import com.example.juniemvc.models.BeerOrderLineDto;
import com.example.juniemvc.repositories.BeerOrderRepository;
import com.example.juniemvc.repositories.BeerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
class BeerOrderServiceImpl implements BeerOrderService {

    private static final int MAX_PAGE_SIZE = 200;
    private static final Logger log = LoggerFactory.getLogger(BeerOrderServiceImpl.class);

    private final BeerOrderRepository orderRepository;
    private final BeerRepository beerRepository;
    private final BeerOrderMapper orderMapper;
    private final BeerOrderLineMapper lineMapper;

    BeerOrderServiceImpl(BeerOrderRepository orderRepository,
                         BeerRepository beerRepository,
                         BeerOrderMapper orderMapper,
                         BeerOrderLineMapper lineMapper) {
        this.orderRepository = orderRepository;
        this.beerRepository = beerRepository;
        this.orderMapper = orderMapper;
        this.lineMapper = lineMapper;
    }

    @Override
    @Transactional
    public BeerOrderDto create(BeerOrderDto dto) {
        BeerOrder entity = orderMapper.toEntity(dto);
        if (entity.getStatus() == null) {
            entity.setStatus(OrderStatus.NEW);
        }
        if (entity.getBeerOrderLines() == null) {
            entity.setBeerOrderLines(new ArrayList<>());
        }
        // Resolve beer references and back-references
        for (BeerOrderLine line : entity.getBeerOrderLines()) {
            resolveBeer(line, null);
            if (line.getStatus() == null) {
                line.setStatus(LineStatus.NEW);
            }
            line.setBeerOrder(entity);
        }
        BeerOrder saved = orderRepository.save(entity);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BeerOrderDto getById(Integer id) {
        BeerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(id)));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BeerOrderDto> list(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        Pageable capped = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        if (log.isDebugEnabled() && pageable.getPageSize() > MAX_PAGE_SIZE) {
            log.debug("Requested page size {} capped to {}", pageable.getPageSize(), MAX_PAGE_SIZE);
        }
        return orderRepository.findAll(capped).map(orderMapper::toDto);
    }

    @Override
    @Transactional
    public BeerOrderDto update(Integer id, BeerOrderDto dto) {
        BeerOrder existing = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(id)));

        // update simple fields
        existing.setCustomerRef(dto.getCustomerRef());
        existing.setPaymentAmount(dto.getPaymentAmount());
        existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());

        // rebuild lines according to DTO
        List<BeerOrderLine> newLines = new ArrayList<>();
        if (dto.getLines() != null) {
            for (BeerOrderLineDto lineDto : dto.getLines()) {
                BeerOrderLine line = lineMapper.toEntity(lineDto);
                resolveBeer(line, id);
                if (line.getStatus() == null) {
                    line.setStatus(LineStatus.NEW);
                }
                line.setBeerOrder(existing);
                newLines.add(line);
            }
        }
        existing.getBeerOrderLines().clear();
        existing.getBeerOrderLines().addAll(newLines);

        BeerOrder saved = orderRepository.save(existing);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BeerOrderDto patch(Integer id, BeerOrderDto dto) {
        BeerOrder existing = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(id)));

        // only patch provided simple fields
        if (dto.getCustomerRef() != null) {
            existing.setCustomerRef(dto.getCustomerRef());
        }
        if (dto.getPaymentAmount() != null) {
            existing.setPaymentAmount(dto.getPaymentAmount());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        // Note: lines patching is out of scope; use dedicated line endpoints

        BeerOrder saved = orderRepository.save(existing);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        BeerOrder existing = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(id)));
        orderRepository.delete(existing);
    }

    @Override
    @Transactional
    public BeerOrderDto addLine(Integer orderId, BeerOrderLineDto lineDto) {
        BeerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(orderId)));
        BeerOrderLine line = lineMapper.toEntity(lineDto);
        resolveBeer(line, orderId);
        if (line.getStatus() == null) line.setStatus(LineStatus.NEW);
        line.setBeerOrder(order);
        order.getBeerOrderLines().add(line);
        BeerOrder saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BeerOrderDto updateLine(Integer orderId, Integer lineId, BeerOrderLineDto lineDto) {
        BeerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(orderId)));
        // find line
        BeerOrderLine line = order.getBeerOrderLines().stream()
                .filter(l -> lineId.equals(l.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("BeerOrderLine %d not found".formatted(lineId)));
        // update fields
        if (lineDto.getBeerId() != null && (line.getBeer() == null || !line.getBeer().getId().equals(lineDto.getBeerId()))) {
            Beer beer = beerRepository.findById(lineDto.getBeerId())
                    .orElseThrow(() -> new EntityNotFoundException("Beer %d not found".formatted(lineDto.getBeerId())));
            line.setBeer(beer);
        }
        if (lineDto.getOrderQuantity() != null) line.setOrderQuantity(lineDto.getOrderQuantity());
        if (lineDto.getQuantityAllocated() != null) line.setQuantityAllocated(lineDto.getQuantityAllocated());
        if (lineDto.getStatus() != null) line.setStatus(lineDto.getStatus());
        BeerOrder saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteLine(Integer orderId, Integer lineId) {
        BeerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("BeerOrder %d not found".formatted(orderId)));
        order.getBeerOrderLines().removeIf(l -> lineId.equals(l.getId()));
        orderRepository.save(order);
    }

    private void resolveBeer(BeerOrderLine line, Integer contextOrderId) {
        if (line.getBeer() == null) {
            throw new EntityNotFoundException("Beer reference is required for line in order %d".formatted(contextOrderId));
        }
        Integer beerId = line.getBeer().getId();
        if (beerId == null) {
            throw new EntityNotFoundException("Beer id is required for line in order %d".formatted(contextOrderId));
        }
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new EntityNotFoundException("Beer %d not found".formatted(beerId)));
        line.setBeer(beer);
    }
}
