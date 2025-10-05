package com.youthcase.orderflow.pr.mapper;

import com.youthcase.orderflow.pr.domain.PurchaseRequest;
import com.youthcase.orderflow.pr.dto.PurchaseRequestDto;

public class PurchaseRequestMapper {
    public static PurchaseRequestDto toDto(PurchaseRequest pr) {
        return new PurchaseRequestDto(
                pr.getId(),
                pr.getStoreId(),
                pr.getGtin(),
                pr.getQty(),
                pr.getExpectedDate(),
                pr.getStatus()
        );
    }
}
