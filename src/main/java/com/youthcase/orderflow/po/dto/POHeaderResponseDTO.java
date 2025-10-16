package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POHeaderResponseDTO {
    private Long poId;
    private POStatus status;
    private Long totalAmount;
    private LocalDate actionDate;
    private String remarks;

    public static POHeaderResponseDTO fromEntity(POHeader header) {
        return POHeaderResponseDTO.builder()
                .poId(header.getPoId())
                .status(header.getStatus())
                .totalAmount(header.getTotalAmount())
                .build();
    }
}