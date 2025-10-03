package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.DTO.ShopListRequestDto;
import com.youthcase.orderflow.pr.DTO.ShopListResponseDto;
import com.youthcase.orderflow.pr.domain.AvailableStatus;
import com.youthcase.orderflow.pr.domain.ShopList;
import com.youthcase.orderflow.pr.service.ShopListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop-lists")
@RequiredArgsConstructor
public class ShopListController {

    private final ShopListService shopListService;

    @PostMapping
    public ShopListResponseDto createShopList(@RequestBody @Valid ShopListRequestDto dto) {
        return shopListService.createShopList(dto);
    }

    @GetMapping
    public List<ShopListResponseDto> getAllShopLists() {
        return shopListService.getAllShopLists();
    }

    @GetMapping("/{id}")
    public ShopListResponseDto getShopList(@PathVariable Long id) {
        return shopListService.getShopListById(id);
    }

    @PutMapping("/{id}")
    public ShopListResponseDto updateShopList(@PathVariable Long id, @RequestBody @Valid ShopListRequestDto dto) {
        return shopListService.updateShopList(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteShopList(@PathVariable Long id) {
        shopListService.deleteShopList(id);
    }
}