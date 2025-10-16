//// src/backend/receipt/dto/GrItemRequest.java
//package com.youthcase.orderflow.gr.dto;
//
//import lombok.*;
//import jakarta.validation.constraints.*;
//import java.time.LocalDate;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class GrItemRequestKD {
//
//    @NotBlank(message = "GTIN은 필수입니다")
//    @Size(min = 14, max = 14, message = "GTIN은 14자리여야 합니다")
//    private String gtin;
//
//    @NotNull(message = "수량은 필수입니다")
//    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
//    private Integer quantity;
//
//    @NotBlank(message = "창고는 필수입니다")
//    private String warehouseId;
//
//    @NotBlank(message = "LOT번호는 필수입니다")
//    private String lotNo;
//
//    @NotNull(message = "유통기한은 필수입니다")
//    @Future(message = "유통기한은 오늘 이후여야 합니다")
//    private LocalDate expiryDate;
//}
