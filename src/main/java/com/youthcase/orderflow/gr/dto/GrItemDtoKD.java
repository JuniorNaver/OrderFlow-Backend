//// src/backend/src/main/java/com/orderflow/receipt/dto/GrItemDto.java
//package com.youthcase.orderflow.gr.dto;
//
//import com.orderflow.receipt.entity.GrItem;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
///**
// * 입고 품목 DTO
// * MM_GR_001: 입고 등록
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class GrItemDtoKD {
//
//    private Long grItemId;
//
//    @NotBlank(message = "상품 코드는 필수입니다")
//    private String productCode;
//
//    private String productName;
//
//    private Long lotId;
//    private String lotNumber;
//
//    @NotNull(message = "순번은 필수입니다")
//    private Integer itemNumber;
//
//    @NotNull(message = "입고 수량은 필수입니다")
//    @Positive(message = "입고 수량은 양수여야 합니다")
//    private Integer quantity;
//
//    @NotNull(message = "매입 단가는 필수입니다")
//    @Positive(message = "매입 단가는 양수여야 합니다")
//    private BigDecimal costPrice;
//
//    private BigDecimal subtotal;
//
//    @NotNull(message = "유통기한은 필수입니다")
//    private LocalDate expiryDate;
//
//    private String barcodeData;
//    private String remarks;
//
//    /**
//     * Entity -> DTO 변환
//     */
//    public static GrItemDtoKD from(GrItem item) {
//        if (item == null) {
//            return null;
//        }
//
//        return GrItemDtoKD.builder()
//                .grItemId(item.getGrItemId())
//                .productCode(item.getProduct().getProductCode())
//                .productName(item.getProduct().getProductName())
//                .lotId(item.getLot() != null ? item.getLot().getLotId() : null)
//                .lotNumber(item.getLot() != null ? item.getLot().getLotNumber() : null)
//                .itemNumber(item.getItemNumber())
//                .quantity(item.getQuantity())
//                .costPrice(item.getCostPrice())
//                .subtotal(item.getSubtotal())
//                .expiryDate(item.getExpiryDate())
//                .barcodeData(item.getBarcodeData())
//                .remarks(item.getRemarks())
//                .build();
//    }
//}
