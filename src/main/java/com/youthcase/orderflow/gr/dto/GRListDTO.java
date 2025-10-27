package com.youthcase.orderflow.gr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class GRListDTO {

    private Long grHeaderId;

    @JsonProperty("poId")
    private Long poId;

    private String externalId;
    private BigDecimal totalAmount;
    private Long totalQty;
    private String userName;
    private GoodsReceiptStatus status;
    private LocalDate receiptDate;
    private LocalDate expectedArrival;

    // ✅ JPQL에서 사용하는 생성자 (순서 정확히 맞춤)
    public GRListDTO(
            Long grHeaderId,
            Long poId,
            String externalId,
            BigDecimal totalAmount,
            Long totalQty,
            String userName,
            GoodsReceiptStatus status,
            LocalDate receiptDate,
            LocalDate expectedArrival
    ) {
        this.grHeaderId = grHeaderId;
        this.poId = poId;
        this.externalId = externalId;
        this.totalAmount = totalAmount;
        this.totalQty = totalQty;
        this.userName = userName;
        this.status = status;
        this.receiptDate = receiptDate;
        this.expectedArrival = expectedArrival;
    }
}
