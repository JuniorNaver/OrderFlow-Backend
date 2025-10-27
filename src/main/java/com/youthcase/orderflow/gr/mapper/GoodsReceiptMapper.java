package com.youthcase.orderflow.gr.mapper;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.gr.domain.*;
import com.youthcase.orderflow.gr.dto.*;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.po.domain.POHeader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GoodsReceiptMapper {

    // ✅ DTO → Entity (등록 시)
    public GoodsReceiptHeader toEntity(
            GoodsReceiptHeaderDTO dto,
            User user,
            Warehouse warehouse,
            POHeader poHeader,
            Map<String, Product> productMap
    ) {
        if (dto == null) return null;

        // ✅ 상태 우선순위: DTO > 기본값(PENDING)
        GoodsReceiptStatus status =
                dto.getStatus() != null
                        ? dto.getStatus()
                        : GoodsReceiptStatus.PENDING;

        GoodsReceiptHeader header = GoodsReceiptHeader.builder()
                .grHeaderId(dto.getId())
                .status(status) // ✅ 수정된 부분
                .receiptDate(dto.getReceiptDate())
                .note(dto.getNote())
                .user(user)
                .poHeader(poHeader)
                .build();

        // 📦 GR_ITEM 변환
        if (dto.getItems() != null) {
            List<GoodsReceiptItem> items = dto.getItems().stream()
                    .map(i -> toItemEntity(i, header, productMap))
                    .collect(Collectors.toList());
            header.setItems(items);
        }

        return header;
    }

    // ✅ GR_ITEM DTO → Entity
    private GoodsReceiptItem toItemEntity(GoodsReceiptItemDTO dto, GoodsReceiptHeader header, Map<String, Product> productMap) {
        GoodsReceiptItem item = GoodsReceiptItem.builder()
                .itemNo(dto.getItemNo())
                .qty(dto.getQty())
                .note(dto.getNote())
                .expiryCalcType(dto.getExpiryCalcType() != null
                        ? com.youthcase.orderflow.gr.status.GRExpiryType.valueOf(dto.getExpiryCalcType())
                        : com.youthcase.orderflow.gr.status.GRExpiryType.MFG_BASED)
                .mfgDate(dto.getMfgDate())
                .expDateManual(dto.getExpDateManual())
                .product(productMap.get(dto.getGtin()))
                .header(header)
                .build();

        // 📦 LOT 매핑
        if (dto.getLots() != null) {
            List<Lot> lots = dto.getLots().stream()
                    .map(l -> toLotEntity(l, item, productMap.get(dto.getGtin())))
                    .collect(Collectors.toList());
            item.setLots(lots);
        }

        return item;
    }

    // ✅ LOT DTO → Entity
    private Lot toLotEntity(LotDTO dto, GoodsReceiptItem item, Product product) {
        return Lot.builder()
                .lotId(dto.getLotId())
                .lotNo(dto.getLotNo())
                .qty(dto.getQty())
                .mfgDate(dto.getMfgDate())
                .expDate(dto.getExpDate())
                .status(dto.getStatus() != null
                        ? com.youthcase.orderflow.gr.status.LotStatus.valueOf(dto.getStatus())
                        : com.youthcase.orderflow.gr.status.LotStatus.ACTIVE)
                .product(product)
                .goodsReceiptItem(item)
                .build();
    }

    // ✅ Entity → DTO (조회 시)
    public GoodsReceiptHeaderDTO toDTO(GoodsReceiptHeader entity) {
        if (entity == null) return null;

        List<GoodsReceiptItemDTO> itemDTOs = entity.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        return GoodsReceiptHeaderDTO.builder()
                .id(entity.getGrHeaderId())
                .status(entity.getStatus())
                .receiptDate(entity.getReceiptDate())
                .note(entity.getNote())
                .poId(entity.getPoHeader() != null ? entity.getPoHeader().getPoId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .items(itemDTOs)
                .build();
    }

    // ✅ GR_ITEM Entity → DTO
    private GoodsReceiptItemDTO toItemDTO(GoodsReceiptItem item) {
        List<LotDTO> lotDTOs = item.getLots() != null
                ? item.getLots().stream().map(this::toLotDTO).collect(Collectors.toList())
                : List.of();

        return GoodsReceiptItemDTO.builder()
                .itemNo(item.getItemNo())
                .gtin(item.getProduct() != null ? item.getProduct().getGtin() : null)
                .qty(item.getQty())
                .note(item.getNote())
                .expiryCalcType(item.getExpiryCalcType() != null ? item.getExpiryCalcType().name() : null)
                .mfgDate(item.getMfgDate())
                .expDateManual(item.getExpDateManual())
                .lots(lotDTOs)
                .build();
    }

    // ✅ LOT Entity → DTO
    private LotDTO toLotDTO(Lot lot) {
        return LotDTO.builder()
                .lotId(lot.getLotId())
                .lotNo(lot.getLotNo())
                .qty(lot.getQty())
                .gtin(lot.getProduct() != null ? lot.getProduct().getGtin() : null)
                .mfgDate(lot.getMfgDate())
                .expDate(lot.getExpDate())
                .status(lot.getStatus().name())
                .build();
    }
}
