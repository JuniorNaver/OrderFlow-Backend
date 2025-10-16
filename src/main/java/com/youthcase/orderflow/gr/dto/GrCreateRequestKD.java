//// src/backend/receipt/dto/GrCreateRequest.java
//package com.youthcase.orderflow.gr.dto;
//
//import lombok.*;
//import jakarta.validation.constraints.*;
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * 입고 등록 요청 DTO
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class GrCreateRequestKD {
//
//    @NotBlank(message = "발주번호는 필수입니다")
//    private String poHeaderId;              // 발주내역 ID
//
//    @NotNull(message = "입고일자는 필수입니다")
//    private LocalDateTime grDate;           // 입고일자
//
//    @NotBlank(message = "담당자는 필수입니다")
//    private String accountId;               // 계정 ID
//
//    private String remark;                  // 비고
//
//    @NotEmpty(message = "입고 아이템은 최소 1개 이상이어야 합니다")
//    @Valid
//    private List<GrItemRequest> items;      // 입고 아이템 목록
//}
