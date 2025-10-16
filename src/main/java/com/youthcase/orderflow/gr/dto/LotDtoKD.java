//// src/backend/src/main/java/com/orderflow/receipt/dto/LotDto.java
//package com.youthcase.orderflow.gr.dto;
//
//import com.orderflow.receipt.entity.Lot;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
///**
// * 로트 DTO
// * MM_GR_004: LOT 관리
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class LotDtoKD {
//
//    private Long lotId;
//
//    private String lotNumber;
//
//    @NotBlank(message = "상품 코드는 필수입니다")
//    private String productCode;
//
//    private String productName;
//
//    private LocalDate manufactureDate;
//
//    @NotNull(message = "유통기한은 필수입니다")
//    private LocalDate expiryDate;
//
//    private String supplier;
//
//    @Positive(message = "초기 수량은 양수여야 합니다")
//    private Integer initialQuantity;
//
//    private Integer currentQuantity;
//
//    @Builder.Default
//    private String status = "AVAILABLE";
//
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//    // 유통기한까지 남은 일수
//    private Long daysUntilExpiry;
//
//    /**
//     * Entity -> DTO 변환
//     */
//    public static LotDtoKD from(Lot lot) {
//        if (lot == null) {
//            return null;
//        }
//
//        LotDtoKD dto = LotDtoKD.builder()
//                .lotId(lot.getLotId())
//                .lotNumber(lot.getLotNumber())
//                .productCode(lot.getProduct().getProductCode())
//                .productName(lot.getProduct().getProductName())
//                .manufactureDate(lot.getManufactureDate())
//                .expiryDate(lot.getExpiryDate())
//                .supplier(lot.getSupplier())
//                .initialQuantity(lot.getInitialQuantity())
//                .currentQuantity(lot.getCurrentQuantity())
//                .status(lot.getStatus().name())
//                .createdAt(lot.getCreatedAt())
//                .updatedAt(lot.getUpdatedAt())
//                .build();
//
//        // 유통기한까지 남은 일수 계산
//        if (lot.getExpiryDate() != null) {
//            dto.setDaysUntilExpiry(
//                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), lot.getExpiryDate())
//            );
//        }
//
//        return dto;
//    }
//}
