package com.youthcase.orderflow.gr.mapper;

import com.youthcase.orderflow.gr.domain.*;
import com.youthcase.orderflow.gr.dto.*;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.master.domain.Product;
import com.youthcase.orderflow.master.domain.Warehouse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoodsReceiptMapper {

    public GoodsReceiptHeader toEntity(GoodsReceiptHeaderDTO dto, User user, Warehouse warehouse, POHeader poHeader, List<Product> products) {
        GoodsReceiptHeader header = GoodsReceiptHeader.builder()
                .id(dto.getId())
                .status(dto.getStatus())
                .receiptDate(dto.getReceiptDate())
                .note(dto.getNote())
                .user(user)
                .warehouse(warehouse)
                .poHeader(poHeader)
                .build();

        if (dto.getItems() != null) {
            List<GoodsReceiptItem> items = dto.getItems().stream()
                    .map(i -> GoodsReceiptItem.builder()
                            .itemNo(i.getItemNo())
                            .qty(i.getQty())
                            .note(i.getNote())
                            .product(products.stream()
                                    .filter(p -> p.getGtin().equals(i.getGtin()))
                                    .findFirst()
                                    .orElse(null))
                            .header(header)
                            .build())
                    .collect(Collectors.toList());
            header.setItems(items);
        }

        return header;
    }

    public GoodsReceiptHeaderDTO toDTO(GoodsReceiptHeader entity) {
        if (entity == null) return null;

        List<GoodsReceiptItemDTO> itemDTOs = entity.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        return GoodsReceiptHeaderDTO.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .receiptDate(entity.getReceiptDate())
                .note(entity.getNote())
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getWarehouseId() : null)
                .poId(entity.getPoHeader() != null ? entity.getPoHeader().getPoId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .items(itemDTOs)
                .build();
    }

    private GoodsReceiptItemDTO toItemDTO(GoodsReceiptItem item) {
        return GoodsReceiptItemDTO.builder()
                .itemNo(item.getItemNo())
                .qty(item.getQty())
                .note(item.getNote())
                .gtin(item.getProduct() != null ? item.getProduct().getGtin() : null)
                .build();
    }
    
}
