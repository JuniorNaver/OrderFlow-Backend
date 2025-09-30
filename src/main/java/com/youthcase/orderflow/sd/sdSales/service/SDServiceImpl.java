package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.sd.sdSales.domain.MmStock;
import com.youthcase.orderflow.sd.sdSales.domain.ProductMaster;
import com.youthcase.orderflow.sd.sdSales.domain.SalesHeader;
import com.youthcase.orderflow.sd.sdSales.domain.SalesItem;
import com.youthcase.orderflow.sd.sdSales.repository.MmStockRepository;
import com.youthcase.orderflow.sd.sdSales.repository.ProductRepository;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import com.youthcase.orderflow.sd.sdSales.repository.SalesItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SDServiceImpl implements SDService {
    private final SalesHeaderRepository salesHeaderRepository;
    private final SalesItemRepository salesItemRepository;
    private final ProductRepository productRepository;
    private final MmStockRepository mmStockRepository;

    //salesHeader 주문 생성
    @Override
    public SalesHeader createOrder() {
        SalesHeader header = new SalesHeader();
        header.setSalesDate(LocalDateTime.now());
        header.setSalesStatus("IN_PROGRESS"); //주문 진행중 상태~~~
        return salesHeaderRepository.save(header);
    }
    //salesHeader 바코드로 아이템 추가
    @Override
    @Transactional
    public void addItemByBarcode(Long orderId, String gtin, int quantity) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        MmStock mmStock = mmStockRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new RuntimeException("재고 없음"));

        if (mmStock.getQuantity() < quantity) {
            throw new RuntimeException("재고 부족");
        }

        ProductMaster product = mmStock.getProduct();

        SalesItem item = new SalesItem();
        item.setSalesHeader(header);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setSdPrice(product.getBasePrice().multiply(BigDecimal.valueOf(quantity)));

        header.getItems().add(item);
        mmStock.setQuantity(mmStock.getQuantity() - quantity);

        salesHeaderRepository.save(header);
        mmStockRepository.save(mmStock);
    }

    //salesHeader 주문에 속한 아이템 목록 조회
    @Override
    public List<SalesItem> getItemsByOrderId(Long orderId) {
        return salesItemRepository.findBySalesHeaderOrderId(orderId);
    }

    //salesHeader 주문완료
    @Override
    public void completeOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        header.setSalesStatus("COMPLETED");
        salesHeaderRepository.save(header);
    }

    //salesItem 재고 수정처리
    @Override
    @Transactional
    public void updateItemQuantity(Long orderId, String gtin, int newQuantity) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        SalesItem item = header.getItems().stream()
                .filter(i -> i.getProduct().getGtin().equals(gtin))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 상품 없음"));

        MmStock mmStock = mmStockRepository.findByProduct_Gtin(gtin)
                .orElseThrow(() -> new RuntimeException("재고 없음"));

        int currentQty = item.getQuantity();
        int diff = newQuantity - currentQty;

        if (diff > 0) {
            if (mmStock.getQuantity() < diff) {
                throw new RuntimeException("재고부족");
            }
            mmStock.setQuantity(mmStock.getQuantity() - diff);
        } else if (diff < 0) {
            mmStock.setQuantity(mmStock.getQuantity() + Math.abs(diff));
        }

        item.setQuantity(newQuantity);
        item.setSdPrice(item.getProduct().getBasePrice().multiply(BigDecimal.valueOf(newQuantity)));

        salesHeaderRepository.save(header);
        mmStockRepository.save(mmStock);
    }

    //보류처리
    @Override
    @Transactional
    public void holdOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));
        header.setSalesStatus("ON_HOLD");
        salesHeaderRepository.save(header);
    }

    //보류 목록 불러오기
    @Override
    public List<SalesHeader> getHoldOrders() {
        return salesHeaderRepository.findBySalesStatus("ON_HOLD");
    }

    //보류된 주문 다시 열기
    @Override
    @Transactional
    public void resumeOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));
        header.setSalesStatus("In_PROGRESS");
        salesHeaderRepository.save(header);
    }

    //보류 주문 취소
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));
        if (!"ON_HOLD".equals(header.getSalesStatus())) {
            throw new RuntimeException("보류 상태가 아닌 주문은 취소 불가");
        }
        salesHeaderRepository.delete(header);
    }
}
