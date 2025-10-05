package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.dto.ShopListItemDto;
import com.youthcase.orderflow.pr.service.ShopListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/pr/shop-list")
@RequiredArgsConstructor
public class ShopListController {
    private final ShopListService shopListService;

    @GetMapping
    public Page<ShopListItemDto> list(
            @RequestParam(required=false) String name,
            @RequestParam(required=false) String gtin,
            @RequestParam(required=false, name="category") String categoryCode,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required=false) LocalDate orderDate,
            @PageableDefault(size=20, sort="productName") Pageable pageable
    ) {
        return shopListService.list(name, gtin, categoryCode, orderDate, pageable);
    }
}