package com.youthcase.orderflow.gr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceiptRequest {

    @NotNull(message = "발주 ID는 필수입니다")
    private Long poId;

    @NotNull(message = "창고 ID는 필수입니다")
    private Long warehouseId;

    @NotEmpty(message = "입고 품목은 최소 1개 이상이어야 합니다")
    @Valid
    private List<GrItemRequest> items;

    private String scanType; // MANUAL, GS1_128, EAN_13

    private String remarks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrItemRequest {

        @NotNull(message = "상품 ID는 필수입니다")
        private Long productId;

        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 양수여야 합니다")
        private Integer quantity;

        @NotNull(message = "유통기한은 필수입니다")
        private LocalDate expiryDate;

        private String lotNumber;

        private String barcode;
    }
}
