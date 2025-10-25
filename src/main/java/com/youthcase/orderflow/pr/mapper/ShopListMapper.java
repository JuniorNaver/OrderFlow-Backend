package com.youthcase.orderflow.pr.mapper;

import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.pr.domain.ShopList;
import com.youthcase.orderflow.pr.dto.ShopListCommandDto;
import com.youthcase.orderflow.pr.dto.ShopListItemDto;

import java.time.LocalDate;

public class ShopListMapper {
    private ShopListMapper() {}

    public static ShopList toEntity(ShopListCommandDto dto, Product product) {
        var sl = new ShopList();
        sl.setProduct(product);
        sl.setDescription(dto.description());
        sl.setOrderable(dto.orderable() != null ? dto.orderable() : Boolean.TRUE);
        // purchasePrice는 @PrePersist에서 비어있으면 product.price로 보정됨
        return sl;
    }

    public static void apply(ShopList sl, ShopListCommandDto dto) {
        if (dto.description() != null) sl.setDescription(dto.description());
        if (dto.orderable()  != null) sl.setOrderable(dto.orderable());
    }

    // orderDate/expectedDueDate는 서비스에서 계산해 전달(없으면 null)
    public static ShopListItemDto toItem(ShopList sl, LocalDate orderDate, LocalDate expectedDueDate) {
        var p = sl.getProduct();
        var c = p.getCategory();
        return new ShopListItemDto(
                sl.getId(),
                p.getGtin(),
                p.getProductName(),
                p.getUnit(),
                sl.getPurchasePrice(),
                p.getStorageMethod(),
                c != null ? c.getKanCode() : null,
                c != null ? c.getLargeCategory() : null,  // 필요에 맞춰 medium/small 교체 가능
                p.getImageUrl(),
                sl.getDescription(),
                sl.getCreatedAt(),
                sl.getUpdatedAt(),
                orderDate,
                expectedDueDate
        );
    }
}

