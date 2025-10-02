package com.youthcase.orderflow.sd.sdSales.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SALES_HEADER")
@SequenceGenerator(
        name = "sales_header_seq",
        sequenceName = "SALES_HEADER_SEQ",
        initialValue = 1,
        allocationSize = 1)
public class SalesHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_header_seq")
    private Long orderId;

    @ColumnDefault("sysdate")
    @Column(name= "SALES_DATE", insertable = false, updatable = false)
    private LocalDateTime salesDate;

    @Column(name= "TOTAL_AMOUNT", precision= 12, scale=2 , nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private SalesStatus salesStatus;

    // 1:N 매핑 (헤더 ↔ 아이템)
    @OneToMany(mappedBy = "salesHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesItem> salesItems = new ArrayList<>();


}
