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
 * - 장바구니(PR) 단계에서 아이템 추가/수정 요청 시 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POItemRequestDTO {

    private Long itemNo;        // 아이템 번호 (수정 시 필요)
    private String gtin;        // 상품 식별자 (FK)
    private Long orderQty;      // 발주 수량
    private BigDecimal unitPrice; // 매입 단가 (스냅샷)

    /**
     * ✅ DTO → Entity 변환
     * - Product, POHeader는 서비스 단에서 조회 후 주입해야 함
     * - 가격/수량/총액은 여기서 계산 및 설정
     */
    public POItem toEntity(POHeader header, Product product) {
        BigDecimal total = (unitPrice != null && orderQty != null)
                ? unitPrice.multiply(BigDecimal.valueOf(orderQty))
                : BigDecimal.ZERO;

        return POItem.builder()
                .poHeader(header)
                .product(product)
                .orderQty(orderQty)
                .pendingQty(orderQty)              // 초기 미출수량 = 발주수량
                .shippedQty(0L)                    // 초기 출고수량 = 0
                .purchasePrice(unitPrice)          // 단가 스냅샷
                .total(total)                      // 합계 계산
                .expectedArrival(LocalDate.now().plusDays(3)) // 기본 예상 도착일
                .status(POStatus.PR)               // 초기 상태 = 장바구니(PR)
                .build();
    }
}