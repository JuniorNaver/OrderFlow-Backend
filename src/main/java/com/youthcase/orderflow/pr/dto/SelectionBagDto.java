package com.youthcase.orderflow.pr.dto;

import java.util.List;

public record SelectionBagDto(
        String ownerId, List<SelectionItemDto> items) {}
