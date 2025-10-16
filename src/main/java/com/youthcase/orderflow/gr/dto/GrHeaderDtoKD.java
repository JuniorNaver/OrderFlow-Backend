//// src/backend/src/main/java/com/orderflow/receipt/dto/GrHeaderDto.java
//package com.youthcase.orderflow.gr.dto;
//
//import com.orderflow.receipt.entity.GrHeader;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import jakarta.validation.constraints.NotNull;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 입고 헤더 DTO
// * MM_GR_001: 입고 등록
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class GrHeaderDto {
//
//    private Long grId;
//
//    private String grNumber;
//
//    private Long poId;
//    private String poNumber;
//
//    @NotNull(message = "점포 ID는 필수입니다")
//    private Long storeId;
//
//    private Long userId;
//    private String userName;
//
//    @NotNull(message = "입고 일자는 필수입니다")
//    private LocalDate grDate;
//
//    private LocalDateTime grTime;
//
//    private BigDecimal totalAmount;
//    private Integer totalQuantity;
//
//    @Builder.Default
//    private String status = "COMPLETED";
//
//    private String scanType;
//
//    private String remarks;
//
//    private List<GrItemDto> items;
//
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//    /**
//     * Entity -> DTO 변환
//     */
//    public static GrHeaderDto from(GrHeader grHeader) {
//        if (grHeader == null) {
//            return null;
//        }
//
//        return GrHeaderDto.builder()
//                .grId(grHeader.getGrId())
//                .grNumber(grHeader.getGrNumber())
//                .poId(grHeader.getPoHeader() != null ? grHeader.getPoHeader().getPoId() : null)
//                .poNumber(grHeader.getPoHeader() != null ? grHeader.getPoHeader().getPoNumber() : null)
//                .storeId(grHeader.getStoreId())
//                .userId(grHeader.getUserId())
//                .grDate(grHeader.getGrDate())
//                .grTime(grHeader.getGrTime())
//                .totalAmount(grHeader.getTotalAmount())
//                .totalQuantity(grHeader.getTotalQuantity())
//                .status(grHeader.getStatus().name())
//                .scanType(grHeader.getScanType() != null ? grHeader.getScanType().name() : null)
//                .remarks(grHeader.getRemarks())
//                .items(grHeader.getItems().stream()
//                        .map(GrItemDto::from)
//                        .collect(Collectors.toList()))
//                .createdAt(grHeader.getCreatedAt())
//                .updatedAt(grHeader.getUpdatedAt())
//                .build();
//    }
//
//    /**
//     * Entity -> DTO 변환 (간략 버전 - items 제외)
//     */
//    public static GrHeaderDto fromSummary(GrHeader grHeader) {
//        if (grHeader == null) {
//            return null;
//        }
//
//        return GrHeaderDto.builder()
//                .grId(grHeader.getGrId())
//                .grNumber(grHeader.getGrNumber())
//                .poId(grHeader.getPoHeader() != null ? grHeader.getPoHeader().getPoId() : null)
//                .poNumber(grHeader.getPoHeader() != null ? grHeader.getPoHeader().getPoNumber() : null)
//                .storeId(grHeader.getStoreId())
//                .userId(grHeader.getUserId())
//                .grDate(grHeader.getGrDate())
//                .grTime(grHeader.getGrTime())
//                .totalAmount(grHeader.getTotalAmount())
//                .totalQuantity(grHeader.getTotalQuantity())
//                .status(grHeader.getStatus().name())
//                .scanType(grHeader.getScanType() != null ? grHeader.getScanType().name() : null)
//                .remarks(grHeader.getRemarks())
//                .createdAt(grHeader.getCreatedAt())
//                .updatedAt(grHeader.getUpdatedAt())
//                .build();
//    }
//}
