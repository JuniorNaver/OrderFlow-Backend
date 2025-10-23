package com.youthcase.orderflow.gr.domain;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.master.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class STKHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stk_history_seq")
    @SequenceGenerator(name = "stk_history_seq", sequenceName = "SEQ_STK_HISTORY", allocationSize = 1)
    private Long id;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private String warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_GTIN", referencedColumnName = "GTIN")
    private Product product;

    @Column(name = "LOT_ID")
    private Long lotId;

    @Column(name = "ACTION_TYPE", length = 30, nullable = false)
    private String actionType; // e.g., "IN", "OUT", "ADJUST"

    @Column(name = "CHANGE_QTY", nullable = false)
    private Long changeQty;

    @Column(name = "ACTION_DATE", nullable = false)
    private LocalDateTime actionDate;

    @Column(name = "NOTE", length = 200)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMED_BY")
    private User performedBy;

    @PrePersist
    public void onCreate() {
        if (this.actionDate == null) {
            this.actionDate = LocalDateTime.now();
        }
    }
}