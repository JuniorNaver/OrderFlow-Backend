package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POItemResponseDTO {
    private Long itemNo;
    private LocalDate expectedArrival;
    private BigDecimal purchasePrice;
    private String productName;
    private Long orderQty;
    private Long pendingQty;
    private Long shippedQty;
    private BigDecimal total;
    private Long poId;           // ✅ 엔티티 대신 ID만 전달
    private String gtin;
    private POStatus status;

    /**
     * ✅ Entity → DTO 변환
     * - 서비스 레이어에서 간단히 static 호출 가능
     */
    public static POItemResponseDTO from(POItem item) {
        if (item == null) return null;

        return POItemResponseDTO.builder()
                .itemNo(item.getItemNo())
                .gtin(item.getProduct().getGtin())
                .productName(item.getProduct().getProductName())
                .purchasePrice(item.getPurchasePrice())
                .orderQty(item.getOrderQty())
                .pendingQty(item.getPendingQty())
                .shippedQty(item.getShippedQty())
                .total(item.getTotal())
                .expectedArrival(item.getExpectedArrival())
                .status(item.getStatus())
                .poId(item.getPoHeader() != null ? item.getPoHeader().getPoId() : null)
                .build();
    }
}