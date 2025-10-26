package com.youthcase.orderflow.gr.dto;

import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * GR(입고) 모듈에서 사용하는 발주 데이터 전용 DTO
 * — PO 모듈을 건드리지 않고, 필요한 데이터만 추출해서 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class POForGRDTO {

    private Long poId;
    private String name; // 발주 담당자
    private BigDecimal totalAmount;
    private String status;
    private List<POItemResponseDTO> items; // PO 모듈의 DTO 그대로 재사용

}