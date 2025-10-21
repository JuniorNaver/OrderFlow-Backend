package com.youthcase.orderflow.gr.dto;

import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoodsReceiptResponse {

    // 📑 입고 문서 번호 (PK)
    private Long id;

    // 🧾 상태 (예: CREATED, COMPLETED, CANCELED)
    private GoodsReceiptStatus status;

    // 📅 입고일자
    private LocalDate receiptDate;

    // 🏭 창고 정보
    private String warehouseId;

    // 🧾 구매 주문 참조
    private Long poId;

    // 👤 담당자
    private String userId;

    // 🗒️ 비고
    private String note;

    // 📦 입고된 상품 목록
    private List<GoodsReceiptItemDTO> items;
}
