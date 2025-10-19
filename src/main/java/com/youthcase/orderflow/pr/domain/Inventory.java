package com.youthcase.orderflow.pr.domain;

import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "INVENTORY",
        uniqueConstraints = @UniqueConstraint(name="UK_INVENTORY_GTIN", columnNames = "GTIN"))
@Getter
@Setter
@NoArgsConstructor
public class Inventory {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GTIN", nullable = false, referencedColumnName = "GTIN")
    private Product product;

    @Column(name = "ON_HAND", nullable = false)
    private Integer onHand = 0;

    @Column(name = "RESERVED", nullable = false)
    private Integer reserved = 0;

    @Version
    private Long version;

    @Transient
    public int getAvailable() {
        return (onHand != null ? onHand : 0) - (reserved != null ? reserved : 0);
    }
}
