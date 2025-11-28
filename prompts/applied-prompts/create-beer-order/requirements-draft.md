## Implementation Instructions

The requirements are listed to update the project with the given JPA entities and enums, and to implement RESTful CRUD style controllers for
the new JPA entities.

### 1. Entity classes (with Lombok)

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

#### `Enums`
```java
public enum BeerStyle { LAGER, PILSNER, STOUT, IPA, PALE_ALE, SAISON }
public enum OrderStatus { NEW, VALIDATION_PENDING, VALIDATED, ALLOCATION_PENDING, ALLOCATED, PICKED_UP, CANCELLED }
public enum LineStatus { NEW, ALLOCATED, BACKORDERED, CANCELLED }
```

### 2. Create DTOs
In the model package create DTO POJOs matching the properties of the added JPA entities.

### 3. Create Mapstruct Mappers
Add the necessary mappers for type conversions to and from DTOs / JPA entities.

### 4. Spring Data Repositories
Add Spring Data Repositories for the new JPA entities.

### 5. Implement the Service Layer
Create new service interfaces and implementations to support CRUD operations initiated in controllers.

### 6. Create new Spring MVC Controllers
Create the necessary controllers for the new entities added to the project

### 7. Test Coverage
Add unit tests for the created components. Verify tests are passing.

Provide tests for:
- mappers
- repositories
- services
- controllers

## Implementation Notes

1. **Lombok Annotations**:
    - `@Getter` and `@Setter`: Generate getters and setters
    - `@NoArgsConstructor`: Generate a no-args constructor
    - `@AllArgsConstructor`: Generate a constructor with all fields
    - `@Builder`: Enable the builder pattern
    - `@ToString.Exclude` and `@EqualsAndHashCode.Exclude`: Prevent circular references in toString() and equals()/hashCode()

2. **JPA Annotations**:
    - `@Entity`: Mark class as JPA entity
    - `@MappedSuperclass`: Base class for entities
    - `@Id`: Primary key
    - `@GeneratedValue`: Auto-generate primary key
    - `@Version`: Optimistic locking
    - `@Column`: Column properties
    - `@OneToMany` and `@ManyToOne`: Relationship mappings
    - `@JoinColumn`: Foreign key column
    - `@CreationTimestamp` and `@UpdateTimestamp`: Automatic timestamp management

3. **Bidirectional Relationship Management**:
    - Use helper methods in BeerOrder to maintain both sides of the relationship
    - Use `mappedBy` to indicate the owning side of relationships
    - Use `CascadeType.ALL` and `orphanRemoval = true` for parent-child relationships

4. **Collection Initialization**:
    - Initialize collections to empty sets to avoid null pointer exceptions
    - Use `@Builder.Default` to ensure collections are initialized when using the builder pattern

5. **Data Types**:
    - Use `BigDecimal` for monetary values with appropriate precision and scale
    - Use `LocalDateTime` for date/time fields
