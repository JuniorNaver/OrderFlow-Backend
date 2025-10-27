package com.youthcase.orderflow.gr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class GRListDTO {

    private Long grHeaderId;

    @JsonProperty("poId")
    private Long poId;

    private String externalId;
    private BigDecimal totalAmount;
    private Long totalQty;
    private String userName;
    private GoodsReceiptStatus status;
    private LocalDate receiptDate;
    private LocalDate expectedArrival;

    // ✅ JPQL에서 사용하는 생성자 (QueryProjection)
    public GRListDTO(
            Long grHeaderId,
            Long poId,
            String externalId,
            BigDecimal totalAmount,
            Long totalQty,
            String userName,
            GoodsReceiptStatus status,
            LocalDate receiptDate,
            LocalDate expectedArrival
    ) {
        this.grHeaderId = grHeaderId;
        this.poId = poId;
        this.externalId = externalId;
        this.totalAmount = totalAmount;
        this.totalQty = totalQty;
        this.userName = userName;
        this.status = status;
        this.receiptDate = receiptDate;
        this.expectedArrival = expectedArrival;
    }

    // ✅ 엔티티 → DTO 변환용 정적 메서드 추가 (Service에서 ::from으로 사용)
    public static GRListDTO from(GoodsReceiptHeader entity) {
        if (entity == null) return null;

        return GRListDTO.builder()
                .grHeaderId(entity.getGrHeaderId())
                .poId(entity.getPoHeader() != null ? entity.getPoHeader().getPoId() : null)
                .externalId(entity.getPoHeader() != null ? entity.getPoHeader().getExternalId() : null)
                .totalAmount(entity.getPoHeader() != null ? entity.getPoHeader().getTotalAmount() : null)
                .totalQty(entity.getItems() != null
                        ? entity.getItems().stream().mapToLong(i -> i.getQty() != null ? i.getQty() : 0).sum()
                        : 0)
                .userName(entity.getUser() != null ? entity.getUser().getName() : null)
                .status(entity.getStatus())
                .receiptDate(entity.getReceiptDate())
                .expectedArrival(
                        entity.getItems() != null && !entity.getItems().isEmpty()
                                ? entity.getItems().get(0).getExpDateManual()
                                : null
                )
                .build();
    }
}
