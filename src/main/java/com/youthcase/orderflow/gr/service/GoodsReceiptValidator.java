/*package com.youthcase.orderflow.gr.service;

import com.youthcase.orderflow.gr.dto.GoodsReceiptRequest;
import com.youthcase.orderflow.gr.dto.GoodsReceiptItemDTO;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.gr.repository.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoodsReceiptValidator {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final LotRepository lotRepository;


    public void validateHeader(GoodsReceiptRequest request) {
        if (request == null)
            throw new IllegalArgumentException("입고 요청이 존재하지 않습니다.");

        if (request.getWarehouseId() == null ||
                !warehouseRepository.existsById(request.getWarehouseId())) {
            throw new IllegalArgumentException("유효하지 않은 창고 ID입니다.");
        }

        if (request.getReceivedDate() == null)
            throw new IllegalArgumentException("입고일자가 누락되었습니다.");

        if (request.getItems() == null || request.getItems().isEmpty())
            throw new IllegalArgumentException("입고 품목이 존재하지 않습니다.");
    }

    public void validateItems(List<GoodsReceiptItemDTO> items) {
        for (GoodsReceiptItemDTO item : items) {
            if (item.getGtin() == null || item.getGtin().isBlank())
                throw new IllegalArgumentException("상품 GTIN이 누락되었습니다.");

            if (!productRepository.existsByGtin(item.getGtin()))
                throw new IllegalArgumentException("등록되지 않은 상품입니다: " + item.getGtin());

            if (item.getQty() == null ||
                    BigDecimal.ZERO.compareTo(item.getQty()) >= 0)
                throw new IllegalArgumentException("입고 수량은 0보다 커야 합니다. 상품: " + item.getGtin());

            if (item.getExpDate() != null &&
                    item.getExpDate().isBefore(LocalDate.now()))
                throw new IllegalArgumentException("유통기한이 지난 상품입니다: " + item.getGtin());

            // ✅ LOT 중복 체크 (같은 GTIN + ExpDate 존재 여부)
            boolean duplicateLot = lotRepository
                    .existsByProduct_GtinAndExpDate(item.getGtin(), item.getExpDate());
            if (duplicateLot)
                throw new IllegalArgumentException("이미 동일한 LOT이 존재합니다. 상품: " + item.getGtin());
        }
    }


    public void validateAll(GoodsReceiptRequest request) {
        validateHeader(request);
        validateItems(request.getItems());
    }
}*/