package com.youthcase.orderflow.pr.dto;

import java.time.LocalDate;

public record SelectionItemDto(
        String gtin, Long quantity, LocalDate orderDate) {}
