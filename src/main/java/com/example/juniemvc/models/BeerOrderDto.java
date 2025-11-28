package com.example.juniemvc.models;

import com.example.juniemvc.entities.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class BeerOrderDto {
    //read only
    private Integer id;
    
    private Integer version;
    
    //reference information from customer
    @Size(max = 255)
    private String customerRef;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal paymentAmount;
    
    // enum status of the order
    private OrderStatus status;
    
    // read only created date
    private LocalDateTime createdDate;
    
    // read only update date
    private LocalDateTime updateDate;

    @Builder.Default
    @Valid
    private List<BeerOrderLineDto> lines = new ArrayList<>();
}
