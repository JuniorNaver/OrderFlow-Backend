package com.youthcase.orderflow.sd.sdSales.dto;

import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.domain.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesHeaderDTO {

    // 가변 객체: setter 허용
    private Long orderId;                   // 주문 번호
    private String orderNo;
    private LocalDateTime salesDate;        // 주문 일자
    private BigDecimal totalAmount;         // 총 금액
    private SalesStatus salesStatus;        // 주문 상태
    private List<SalesItemDTO> salesItems = new ArrayList<>();  // 아이템 목록

    // 기본 생성자에서 아이템 리스트 초기화
    public SalesHeaderDTO(Long orderId, String orderNo, LocalDateTime salesDate,
                          BigDecimal totalAmount, SalesStatus salesStatus) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.salesDate = salesDate;
        this.totalAmount = totalAmount;
        this.salesStatus = salesStatus;
    }

    public static SalesHeaderDTO from(SalesHeader entity) {
        if (entity == null) return null;

        SalesHeaderDTO dto = new SalesHeaderDTO(
                entity.getOrderId(),
                entity.getOrderNo(),
                entity.getSalesDate(),
                entity.getTotalAmount(),
                entity.getSalesStatus()
        );

        if (entity.getSalesItems() != null) {
            for (SalesItem item : entity.getSalesItems()) {
                dto.getSalesItems().add(SalesItemDTO.from(item));
            }
        }

        return dto;
    }

}