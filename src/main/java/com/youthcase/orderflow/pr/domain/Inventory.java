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
    private Long onHand = 0L;

    @Column(name = "RESERVED", nullable = false)
    private Long reserved = 0L;

    @Version
    private Long version;

    @Transient
    public Long getAvailable() {
        return (onHand != null ? onHand : 0L) - (reserved != null ? reserved : 0L);
    }
}
