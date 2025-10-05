package com.youthcase.orderflow.pr.dto;

import java.time.LocalDate;

public record SelectionItemDto(
        String gtin, Integer quantity, LocalDate orderDate) {}
