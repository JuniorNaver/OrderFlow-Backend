package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 📤 POItemRequestDTO
 * - 장바구니(PR) 단계에서 상품 추가 또는 수량 변경 요청 시 사용
 * - 클라이언트는 GTIN(상품코드) + 수량(orderQty)만 전달
 * - 단가(unitPrice)는 서버에서 PriceMaster 기준으로 자동 조회 및 반영
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POItemRequestDTO {

    private Long itemNo;    // 아이템 번호 (수정 시 필요)
    private String gtin;    // 상품 식별자 (FK)
    private Long orderQty;  // 발주 수량

    /**
     * ✅ DTO → Entity 변환
     * - Product, POHeader, 단가는 서비스에서 주입해야 함
     * - 단가, 합계, 상태 등은 서버 내부 로직에서 계산
     */
    public POItem toEntity(POHeader header, Product product, BigDecimal purchasePrice) {
        BigDecimal total = purchasePrice.multiply(BigDecimal.valueOf(orderQty));

        return POItem.builder()
                .poHeader(header)
                .product(product)
                .orderQty(orderQty)
                .pendingQty(orderQty)
                .shippedQty(0L)
                .purchasePrice(purchasePrice) // ✅ 서버에서 주입한 단가
                .total(total)
                .expectedArrival(LocalDate.now().plusDays(3))
                .status(POStatus.PR)
                .build();
    }
}
