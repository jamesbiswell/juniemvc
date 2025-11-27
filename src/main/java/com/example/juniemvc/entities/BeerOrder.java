package com.example.juniemvc.entities;

import com.example.juniemvc.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @Column(length = 255)
    private String customerRef;

    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "beerOrder", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<BeerOrderLine> beerOrderLines = new ArrayList<>();

    public void addLine(BeerOrderLine line) {
        if (line == null) return;
        line.setBeerOrder(this);
        this.beerOrderLines.add(line);
    }

    public void removeLine(BeerOrderLine line) {
        if (line == null) return;
        line.setBeerOrder(null);
        this.beerOrderLines.remove(line);
    }
}
