package com.youthcase.orderflow.pr.DTO;


import com.youthcase.orderflow.pr.domain.AvailableStatus;
import com.youthcase.orderflow.pr.domain.Unit;

import java.time.LocalDate;

public record ShopListResponseDto(
        Long prItemId,
        String productImage,
        String productDescription,
        LocalDate orderDate,
        String deliveryMessage,
        Unit unit,
        AvailableStatus available
) {}