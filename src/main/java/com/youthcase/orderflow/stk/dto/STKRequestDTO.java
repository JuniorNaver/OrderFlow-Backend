package com.youthcase.orderflow.stk.dto;

import com.youthcase.orderflow.stk.domain.StockStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class STKRequestDTO {

    // --------------------------------------------------
    // ⭐️ 1. FK ID 필드 (엔티티 대신 ID로 받음)
    // --------------------------------------------------

    // Product (GTIN)
    @NotBlank(message = "상품 GTIN은 필수입니다.")
    private String productGtin;

    // Warehouse (창고 ID) - Long -> String으로 변경!
    @NotBlank(message = "창고 ID는 필수입니다.") // Long 대신 String이므로 @NotNull 대신 @NotBlank 사용
    private String warehouseId; // ⭐️ 타입을 String으로 변경 ⭐️

    // Lot (랏 ID - 입고 시점에 생성되므로, 재고 등록 시에는 기존 랏을 참조하거나 새로운 랏을 생성해야 함)
    @NotNull(message = "랏 ID는 필수입니다.")
    private Long lotId;

    // GoodsReceiptHeader (입고 헤더 ID - 재고 등록이 입고 과정의 일부일 경우 사용)
    @NotNull(message = "입고 헤더 ID는 필수입니다.")
    private Long grHeaderId;


    // --------------------------------------------------
    // ⭐️ 2. STK 엔티티 자체 필드
    // --------------------------------------------------

    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 0보다 커야 합니다.")
    private Long quantity;

    @NotNull(message = "재고 상태는 필수입니다.")
    private StockStatus status; // ✅ String → StockStatus 변경

    private Boolean hasExpirationDate = false; // 기본값 설정

    // 재고 위치 코드 (STK 엔티티의 location 필드에 매핑)
    @NotBlank(message = "위치 코드는 필수입니다.")
    private String location;

    // 참고: lastUpdatedAt, stkId는 서비스/DB에서 처리하므로 제외합니다.
    // 참고: isRelocationNeeded는 기본값이 있으므로 제외합니다.
}