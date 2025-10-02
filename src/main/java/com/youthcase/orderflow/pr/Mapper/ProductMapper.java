package com.youthcase.orderflow.pr.Mapper;

import com.youthcase.orderflow.pr.DTO.ProductRequestDto;
import com.youthcase.orderflow.pr.DTO.ProductUpdateDto;
import com.youthcase.orderflow.pr.domain.Product;

public class ProductMapper {

    public static Product toEntity(ProductRequestDto dto, String gtin) {
        Product product = new Product();
        product.setGtin(gtin != null ? gtin : dto.gtin());
        product.setProductName(dto.productName());
        product.setUnit(dto.unit());
        product.setPrice(dto.price());
        product.setStorageMethod(dto.storageMethod());
        return product;
    }

    // 기존 엔티티에 update DTO 반영
    public static void updateEntity(Product product, ProductUpdateDto dto) {
        product.setProductName(dto.productName());
        product.setUnit(dto.unit());
        product.setPrice(dto.price());
        product.setStorageMethod(dto.storageMethod());
    }
}