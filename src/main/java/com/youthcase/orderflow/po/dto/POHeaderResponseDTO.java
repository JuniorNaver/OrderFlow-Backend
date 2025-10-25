package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POHeaderResponseDTO {

    private Long poId;
    private POStatus status;
    private BigDecimal totalAmount;
    private LocalDate actionDate;
    private String remarks;
    private String externalId;

    /** ✅ Entity → DTO 변환 */
    public static POHeaderResponseDTO from(POHeader header) {
        if (header == null) return null;

        return POHeaderResponseDTO.builder()
                .poId(header.getPoId())
                .status(header.getStatus())
                .totalAmount(header.getTotalAmount())
                .actionDate(header.getActionDate())
                .remarks(header.getRemarks())
                .externalId(header.getExternalId())
                .build();
    }
}
