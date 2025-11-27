### What the ERD says (relationships)
- One BeerOrder has many BeerOrderLine rows.
- Each BeerOrderLine belongs to exactly one BeerOrder.
- Each BeerOrderLine refers to exactly one Beer.
- Optionally, a Beer can be referenced by many BeerOrderLine rows.

This is a classic 1–N from `BeerOrder` to `BeerOrderLine`, plus another 1–N from `Beer` to `BeerOrderLine`.

### Recommended mapping decisions
- Use `jakarta.persistence` (Spring Boot 3+).
- Use `@Version` for optimistic locking as shown in the ERD.
- Use `LocalDateTime` timestamps with automatic auditing via Hibernate annotations (`@CreationTimestamp`, `@UpdateTimestamp`) or JPA callbacks.
- Keep collections lazy (default) and `@ManyToOne(fetch = LAZY)` to prevent N+1s.
- Cascade from `BeerOrder -> beerOrderLines` only. Do not cascade to `Beer` through order lines.
- Maintain the bidirectional association (`BeerOrder <-> BeerOrderLine`) with helper methods on the aggregate root (`BeerOrder`).
- Use enums for `status` and `beerStyle` but store as strings: `@Enumerated(EnumType.STRING)`.
- Avoid Lombok cycles in `toString`/`equals` by excluding back-references.

### Entity classes (with Lombok)

#### `Beer`
```java
package com.example.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "beer")
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String beerName;

    @Enumerated(EnumType.STRING)
    private BeerStyle beerStyle; // define enum BeerStyle { LAGER, IPA, ... }

    @Column(unique = true, length = 255)
    private String upc;

    private Integer quantityOnHand;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    // Optional back-reference if you need it (usually not required):
    @OneToMany(mappedBy = "beer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BeerOrderLine> beerOrderLines;
}
```

#### `BeerOrder`
```java
package com.example.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "beer_order")
public class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String customerRef;

    @Column(precision = 19, scale = 2)
    private BigDecimal paymentAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // define enum OrderStatus { NEW, PAID, ALLOCATED, ... }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @OneToMany(
            mappedBy = "beerOrder",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BeerOrderLine> beerOrderLines = new ArrayList<>();

    // Helper methods to keep both sides in sync
    public void addLine(BeerOrderLine line) {
        beerOrderLines.add(line);
        line.setBeerOrder(this);
    }

    public void removeLine(BeerOrderLine line) {
        beerOrderLines.remove(line);
        line.setBeerOrder(null);
    }
}
```

#### `BeerOrderLine`
```java
package com.example.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "beer_order_line")
public class BeerOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beer_order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_beer_order_line_order"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BeerOrder beerOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beer_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_beer_order_line_beer"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Beer beer;

    private Integer orderQuantity;

    private Integer quantityAllocated;

    @Enumerated(EnumType.STRING)
    private LineStatus status; // define enum LineStatus if needed

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
```

#### Enums (example)
```java
public enum BeerStyle { LAGER, PILSNER, STOUT, IPA, PALE_ALE, SAISON }
public enum OrderStatus { NEW, VALIDATION_PENDING, VALIDATED, ALLOCATION_PENDING, ALLOCATED, PICKED_UP, CANCELLED }
public enum LineStatus { NEW, ALLOCATED, BACKORDERED, CANCELLED }
```

### JSON serialization tip (if exposing entities)
If you ever serialize entities directly (not recommended), prevent recursion:
- Use `@JsonManagedReference` on `BeerOrder.beerOrderLines` and `@JsonBackReference` on `BeerOrderLine.beerOrder`.
- Or simply `@JsonIgnore` for back-refs. Prefer mapping to DTOs as per the provided guidelines.

### Database schema notes
- Tables: `beer`, `beer_order`, `beer_order_line`.
- FKs: `beer_order_line.beer_order_id -> beer_order.id`; `beer_order_line.beer_id -> beer.id`.
- Indexes: create indexes on `beer_order_id`, `beer_id`, and `upc` (unique) for lookup performance.

### Repositories (Spring Data JPA)
```java
public interface BeerRepository extends JpaRepository<Beer, Integer> {}
public interface BeerOrderRepository extends JpaRepository<BeerOrder, Integer> {}
public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, Integer> {}
```

### Service-layer transaction boundary
- Wrap create/update flows in a single `@Transactional` service method.
- Example: create an order, add lines via `addLine()`, persist `BeerOrder` only; cascading will persist the lines.

```java
@Service
class BeerOrderService {
    private final BeerOrderRepository orders;
    private final BeerRepository beers;

    BeerOrderService(BeerOrderRepository orders, BeerRepository beers) {
        this.orders = orders;
        this.beers = beers;
    }

    @Transactional
    BeerOrder createOrder(String customerRef, List<OrderItemDto> items) {
        BeerOrder order = new BeerOrder();
        order.setCustomerRef(customerRef);
        order.setStatus(OrderStatus.NEW);

        for (OrderItemDto i : items) {
            Beer beer = beers.getReferenceById(i.beerId());
            BeerOrderLine line = BeerOrderLine.builder()
                    .beer(beer)
                    .orderQuantity(i.qty())
                    .status(LineStatus.NEW)
                    .build();
            order.addLine(line);
        }
        return orders.save(order);
    }
}
```

### Testing the mapping quickly
- Persist a `Beer`, create a `BeerOrder`, add one or more `BeerOrderLine` (hooked to the beer), `orders.save(order)`; verify that lines are saved and carry FK values.
- Fetch `BeerOrder` and ensure `beerOrder.getBeerOrderLines()` loads lazily within a transaction.

### Common pitfalls to avoid
- Forgetting to set both sides of the association; always use `BeerOrder.addLine(line)`.
- Cascading from `BeerOrderLine` to `Beer` (don’t do this—beers are independent master data).
- Using eager fetch on `@ManyToOne` and causing N+1 queries.
- Infinite recursion in Lombok `toString` and JSON serialization—exclude back-refs.

### Summary
Implement the ERD with:
- `BeerOrder (1) -> (N) BeerOrderLine` via `@OneToMany(mappedBy = "beerOrder", cascade = PERSIST|MERGE|REMOVE, orphanRemoval = true)`.
- `BeerOrderLine (N) -> (1) Beer` via `@ManyToOne(fetch = LAZY)`.
- Use Lombok to reduce boilerplate and helper methods to maintain the bidirectional link. Persist only the aggregate root (`BeerOrder`) in write flows.
