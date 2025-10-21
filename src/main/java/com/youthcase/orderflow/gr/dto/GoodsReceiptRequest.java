package com.youthcase.orderflow.gr.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoodsReceiptRequest {

    // 🏭 어떤 창고에 입고되는가
    private String warehouseId;

    // 🧾 구매 주문서(PO)와 연동되는 경우
    private Long poId;

    // 👤 등록자 ID (로그인 사용자)
    private String userId;

    // 📅 입고일자
    private LocalDate receiptDate;

    // 🗒️ 메모 또는 특이사항
    private String note;

    // 📦 입고 상품 목록 (필수)
    private List<GoodsReceiptItemDTO> items;
}