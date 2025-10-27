package com.youthcase.orderflow.stk.dto;

import com.youthcase.orderflow.stk.domain.STK;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate; // LocalDate import 추가

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {

    // 1. STK 고유 정보
    private Long stkId;
    private Long quantity;
    private String status;

    // 2. Product 정보
    private String gtin;
    private String name;
    private BigDecimal price;

    // 3. 연관 엔티티 식별자 및 핵심 정보
    private String warehouseId;     //창고 ID
    private Long lotId;             //로트 ID
    private LocalDate expDate;      //유통기한
    private Long grHeaderId;        //입고 헤더 ID

    public static StockResponse fromEntity(STK s) {
        // null 체크를 포함하여 안전하게 연관 정보를 추출합니다.
        Long grHeaderId = s.getGoodsReceipt() != null ? s.getGoodsReceipt().getGrHeaderId() : null;

        return StockResponse.builder()
                // STK 정보
                .stkId(s.getStkId())
                .quantity(s.getQuantity())
                .status(s.getStatus())

                // Product 정보
                .gtin(s.getProduct().getGtin())
                .name(s.getProduct().getProductName())
                .price(s.getProduct().getPrice())

                // 연관 정보 (필수 연관관계는 null 체크 생략, 선택 연관관계는 null 체크 적용)
                .warehouseId(s.getWarehouse().getWarehouseId()) // Warehouse 엔티티에 getWarehouseId()가 있다고 가정
                .lotId(s.getLot().getLotId())
                .expDate(s.getLot().getExpDate())
                .grHeaderId(grHeaderId)

                .build();
    }
}