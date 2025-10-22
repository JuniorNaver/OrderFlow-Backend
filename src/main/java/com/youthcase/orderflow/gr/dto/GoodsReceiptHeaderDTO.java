package com.youthcase.orderflow.gr.dto;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GoodsReceiptHeaderDTO {
    private Long id;
    private GoodsReceiptStatus status;
    private LocalDate receiptDate;
    private String note;
    private String warehouseId;
    private Long poId;
    private String userId;
    private List<GoodsReceiptItemDTO> items;

    public static GoodsReceiptHeaderDTO from(GoodsReceiptHeader entity) {
        return GoodsReceiptHeaderDTO.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .receiptDate(entity.getReceiptDate())
                .note(entity.getNote())
                // 창고 ID
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getWarehouseId() : null)
                // 발주 ID
                .poId(entity.getPoHeader() != null ? entity.getPoHeader().getPoId() : null)
                // ✅ 작성자: User 엔티티의 name을 사용 (또는 userId)
                .userId(entity.getUser() != null ? entity.getUser().getName() : null)
                .build();
    }

}
