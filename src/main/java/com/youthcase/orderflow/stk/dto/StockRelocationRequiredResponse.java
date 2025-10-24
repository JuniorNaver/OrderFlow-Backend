package com.youthcase.orderflow.stk.dto;

import lombok.Value;
import java.time.LocalDate;

@Value
public class StockRelocationRequiredResponse {

    private Long lotId;
    private Long stkId;
    private String productGtin;
    private String productName;

    // ⭐️ 5번째 인자 타입 수정: Long -> String
    private String warehouseId;

    private LocalDate expiryDate;
    private Long quantity;
    private String issueReason;

    // 생성자 시그니처가 다음과 같이 변경됩니다:
    // (Long, Long, String, String, String, LocalDate, Integer, String)
}