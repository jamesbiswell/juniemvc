package com.example.juniemvc.models;

import com.example.juniemvc.entities.enums.LineStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BeerOrderLineDto {
    private Integer id;
    private Integer version;

    @NotNull
    private Integer beerId;

    @NotNull
    @Min(1)
    private Integer orderQuantity;

    @Min(0)
    private Integer quantityAllocated;
    
    // enum status of the line
    private LineStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}
