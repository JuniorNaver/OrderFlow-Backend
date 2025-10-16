package com.youthcase.orderflow.gr.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoodsReceiptHeaderDTO {
    private Long id;
    private String status;
    private LocalDate receiptDate;
    private String note;
    private String warehouseId;
    private Long poId;
    private String userId;
    private List<GoodsReceiptItemDTO> items;
}
