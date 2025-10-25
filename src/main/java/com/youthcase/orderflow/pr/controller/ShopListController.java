package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.dto.ShopListItemDto;
import com.youthcase.orderflow.pr.repository.ShopListRepository;
import com.youthcase.orderflow.pr.service.ShopListService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/pr/shop-list")
@RequiredArgsConstructor
public class ShopListController {

    private final ShopListService shopListService;

    /** 목록 + 필터(name/gtin/category) + (옵션) orderDate */
    @GetMapping
    public Page<ShopListItemDto> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gtin,
            @RequestParam(required = false, name = "category") String categoryCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate,
            @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable
    ) {
        // 서비스에 list(name, gtin, categoryCode, orderDate, pageable) 오버로드 추가해 둔 버전
        return shopListService.list(name, gtin, categoryCode, orderDate, pageable);
    }

    /** 단건 조회(id) */
    @GetMapping("/{id}")
    public ShopListItemDto get(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate
    ) {
        return shopListService.getById(id, orderDate);
    }

    /** 단건 조회(gtin) */
    @GetMapping("/by-gtin/{gtin}")
    public ShopListItemDto getByGtin(
            @PathVariable String gtin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate
    ) {
        return shopListService.getByGtin(gtin, orderDate);
    }

    /** 생성(간단히 쿼리스트링로 받는 형태) */
    @PostMapping
    public ShopListItemDto create(
            @RequestParam String gtin,
            @RequestParam(required = false) @Size(max = 2000) String description,
            @RequestParam(required = false) Boolean orderable
    ) {
        return shopListService.create(gtin, description, orderable);
    }

    /** 수정(부분) */
    @PatchMapping("/{id}")
    public ShopListItemDto update(
            @PathVariable Long id,
            @RequestParam(required = false) @Size(max = 2000) String description,
            @RequestParam(required = false) Boolean orderable
    ) {
        return shopListService.update(id, description, orderable);
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shopListService.delete(id);
    }

    /** 기간 조회: createdAt 기준 */
    @GetMapping("/range")
    public Page<ShopListItemDto> listByCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate,
            @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable
    ) {
        return shopListService.listByCreatedBetween(from, to, pageable, orderDate);
    }

    /** 통계 */
    @GetMapping("/stats/purchase-price")
    public ShopListService.PriceStatsDto purchasePriceStats() {
        return shopListService.purchasePriceStats();
    }

    /** (옵션) 가벼운 목록: 프로젝션 직접 반환 */
    @GetMapping("/rows")
    public Page<ShopListRepository.Row> listRows(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable
    ) {
        return shopListService.listRows(q, pageable);
    }
}