package com.youthcase.orderflow.pr.controller;

import com.youthcase.orderflow.pr.DTO.ShopListRequestDto;
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

    @GetMapping
    public List<ShopList> getAllShopLists() {
        return shopListService.getAllShopLists();
    }

    @GetMapping("/{id}")
    public ShopList getShopList(@PathVariable Long id) {
        return shopListService.getShopListById(id).orElse(null);
    }

    @PostMapping
    public ShopList createShopList(@RequestBody @Valid ShopListRequestDto dto) {
        ShopList shopList = new ShopList();
        shopList.setProductImage(dto.productImage());
        shopList.setProductDescription(dto.productDescription());
        shopList.setAvailable(AvailableStatus.AVAILABLE); // 예시
        shopList.setOrderDate(dto.orderDate());           // DTO에서 발주일 가져오기
        shopList.setProduct();

        return shopListService.createShopListWithDueDate(shopList);
    }

    @PutMapping("/{id}")
    public ShopList updateShopList(@PathVariable Long id, @RequestBody ShopList shopList) {
        shopList.setPrItemId(id);
        return shopListService.saveShopList(shopList);
    }

    @DeleteMapping("/{id}")
    public void deleteShopList(@PathVariable Long id) {
        shopListService.deleteShopList(id);
    }
}
