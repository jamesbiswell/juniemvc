package com.example.juniemvc.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing Beer details at the API boundary.
 *
 * Server-managed fields (do not set on create/update):
 * - id
 * - createdDate
 * - updateDate
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BeerDto {
    
    //read only
    private Integer id;
    private Integer version;
    
    @NotBlank
    private String beerName;
    
    // style of the beer, ALE, PALE ALE, IPA, etc
    @NotBlank
    private String beerStyle;
    
    // Universal Product Code, a 13-digit number assigned to each unique beer product by the Federal Bar Association
    @NotBlank
    private String upc;

    @NotNull
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;

    // read only created date
    private LocalDateTime createdDate;
    
    // read only update date
    private LocalDateTime updateDate;
}
