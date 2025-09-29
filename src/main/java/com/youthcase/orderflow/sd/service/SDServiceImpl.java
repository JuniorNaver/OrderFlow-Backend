package com.youthcase.orderflow.sd.service;

import com.youthcase.orderflow.sd.domain.ProductMaster;
import com.youthcase.orderflow.sd.domain.SalesItem;
import com.youthcase.orderflow.sd.repository.ProductRepository;
import com.youthcase.orderflow.sd.repository.SalesHeaderRepository;
import com.youthcase.orderflow.sd.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SDServiceImpl implements SDService {
    private final SalesHeaderRepository salesHeaderRepository;
    private final SalesItemRepository salesItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<SalesItem> salesItemList(SalesItem salesItem) {
        return salesItemRepository.findAll();
    }

    @Override
    public void salesItemInsert(SalesItem salesItem){
        /*ProductMaster product = ProductRepository.findById(salesItem.getProduct().getGtin())
                        .orElseThrow(() -> new RuntimeException("상품 없음"));

        salesItem.setPrice(product.getBasePrice().multiply(BigDecimal.valueOf(salesItem.getQuantity())));
        salesItem.setProduct(product);
        salesItemRepository.save(salesItem);*/
    }

    @Override
    public SalesItem getSalesItem(Long itemId) {
        throw new UnsupportedOperationException("아직 구현 안 됨");
    }

    @Override
    public SalesItem updateSalesItem(SalesItem salesItem) {
        throw new UnsupportedOperationException("아직 구현 안 됨");
    }

    @Override
    public void deleteSalesItem(Long itemId) {
        throw new UnsupportedOperationException("아직 구현 안 됨");
    }

    @Override
    public List<SalesItem> getItemsByOrderId(Long orderId) {
        throw new UnsupportedOperationException("아직 구현 안 됨");
    }

    @Override
    public void addItemByBarcode(Long orderId, String gtin, int quantity) {
        throw new UnsupportedOperationException("아직 구현 안 됨");
    }
}
