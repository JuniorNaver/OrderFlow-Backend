package com.youthcase.orderflow.po.dto;

import com.youthcase.orderflow.po.domain.Status;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POHeaderResponseDTO {
    private Long poId;
    private Status status;
    private Long totalAmount;
    private LocalDate actionDate;
    private String remarks;
}