package com.youthcase.orderflow.pr.mapper;

import com.youthcase.orderflow.pr.domain.enums.ExpiryType;
import com.youthcase.orderflow.pr.dto.ProductCreateDto;
import com.youthcase.orderflow.pr.dto.ProductResponseDto;
import com.youthcase.orderflow.pr.dto.ProductUpdateDto;
import com.youthcase.orderflow.pr.domain.Category;
import com.youthcase.orderflow.pr.domain.Product;

import java.math.RoundingMode;

public class ProductMapper {
    private ProductMapper() {throw new AssertionError("No ProductMapper instances");}

    public static ProductResponseDto toResp(Product p) {
        // Category 널 세이프 (FK NOT NULL이더라도 방어적으로)
        String categoryCode = null;
        String categoryName = null;
        Category c = p.getCategory();
        if (c != null) {
            // ↓ Category 엔티티의 실제 getter 이름에 맞춰 수정
            categoryCode = c.getKanCode();
            categoryName = c.getLargeCategory();
        }
        return new ProductResponseDto(
                p.getGtin(),
                p.getProductName(),
                p.getUnit(),
                p.getPrice(),
                p.getStorageMethod(),
                categoryCode,
                categoryName,
                p.getExpiryType(),
                p.getShelfLifeDays(),
                p.getOrderable()
        );
    }

    public static Product toEntity(ProductCreateDto dto, Category category) {
        Product product = new Product();
        product.setGtin(dto.gtin());
        product.setProductName(dto.productName());
        product.setUnit(dto.unit());
        product.setPrice(dto.price().setScale(2, RoundingMode.HALF_UP));
        product.setStorageMethod(dto.storageMethod());
        product.setCategory(category);
        product.setExpiryType(dto.expiryType() == null ? ExpiryType.NONE : dto.expiryType());
        product.setShelfLifeDays(dto.shelfLifeDays());
        return product;
    }

    // 기존 엔티티에 update DTO 반영
    public static void updateEntity(Product product, ProductUpdateDto dto, Category category) {
        product.setProductName(dto.productName());
        product.setUnit(dto.unit());
        product.setPrice(dto.price() == null ? null : dto.price().setScale(2, RoundingMode.HALF_UP));
        product.setStorageMethod(dto.storageMethod());
        product.setCategory(category);
        product.setExpiryType(dto.expiryType());     // @NotNull이면 바로 세팅
        product.setShelfLifeDays(dto.shelfLifeDays());
    }
}