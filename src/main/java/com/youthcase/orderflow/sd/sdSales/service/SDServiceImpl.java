package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.sd.sdSales.domain.*;
import com.youthcase.orderflow.sd.sdSales.dto.ConfirmOrderRequest;
import com.youthcase.orderflow.sd.sdSales.dto.SalesHeaderDTO;
import com.youthcase.orderflow.sd.sdSales.dto.SalesItemDTO;
import com.youthcase.orderflow.sd.sdSales.repository.SalesHeaderRepository;
import com.youthcase.orderflow.sd.sdSales.repository.SalesItemRepository;
import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SDServiceImpl implements SDService {
    private final SalesHeaderRepository salesHeaderRepository;
    private final SalesItemRepository salesItemRepository;
    private final ProductRepository productRepository;
    private final STKRepository stkRepository;


    //salesHeader 주문 생성
    @Override
    public SalesHeader createOrder() {
        SalesHeader header = new SalesHeader();
        header.setSalesDate(LocalDateTime.now());
        header.setSalesStatus(SalesStatus.PENDING); //주문 진행중 상태~~~
        return salesHeaderRepository.save(header);
    }
    //salesHeader 바코드로 아이템 추가 + 재고수정 (react+vite 연동)
    @Override
    @Transactional
    public void confirmOrder(ConfirmOrderRequest request) {
        SalesHeader header = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        for (ConfirmOrderRequest.ItemDTO dto : request.getItems()) {
            // 상품 조회
            Product product = productRepository.findByGtin(String.valueOf(dto.getGtin()))
                    .orElseThrow(() -> new RuntimeException("상품 없음"));

            // ✅ JPA 네이밍 방식으로 FIFO/FEFO 재고 조회
            List<STK> stockList = stkRepository
                    .findByProduct_GtinAndQuantityGreaterThanOrderByLot_ExpDateAsc(dto.getGtin(), 0);

            int remaining = dto.getQuantity();

            for (STK stk : stockList) {
                if (remaining <= 0) break;

                int available = stk.getQuantity();
                int deduction = Math.min(available, remaining);

                // 재고 차감
                stk.updateQuantity(available - deduction);

                // 판매 아이템 등록
                SalesItem item = new SalesItem();
                item.setSalesHeader(header);
                item.setProduct(product);      // Product 엔티티 참조
                item.setSalesQuantity(deduction);   // 판매 수량
                item.setSdPrice(dto.getPrice());

                header.getSalesItems().add(item);

                remaining -= deduction;
            }

            if (remaining > 0) {
                throw new RuntimeException("재고 부족");
            }
        }

        header.setSalesStatus(SalesStatus.COMPLETED);
        salesHeaderRepository.save(header);
    }


    //salesHeader 주문에 속한 아이템 목록 조회, 보류도
    @Override
    public List<SalesItemDTO> getItemsByOrderId(Long orderId) {
        return salesItemRepository.findItemsByHeaderId(orderId);
    }

    //salesHeader 주문완료
    @Override
    public void completeOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        header.setSalesStatus(SalesStatus.CANCELLED);
        salesHeaderRepository.save(header);
    }



    //보류처리
    @Override
    @Transactional
    public void holdOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        //상태 검증: 진행중 상태에서만 보류 가능
        if (!SalesStatus.PENDING.equals(header.getSalesStatus())) {
            throw new RuntimeException("진행 중인 주문만 보류할 수 있습니다.");
    }

        header.setSalesStatus(SalesStatus.HOLD);
        salesHeaderRepository.save(header);
}

    //보류 목록 불러오기
    @Override
    public List<SalesHeaderDTO> getHoldOrders() {
        return salesHeaderRepository.findHoldOrders();
    }


    //보류된 주문 다시 열기(가변패턴)
    @Override
    @Transactional
    public SalesHeaderDTO resumeOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));

        if(!SalesStatus.HOLD.equals(header.getSalesStatus())) {
            throw new RuntimeException("보류 상태가 아닌 주문은 다시 열 수 없습니다.");
        }

        header.setSalesStatus(SalesStatus.PENDING);

        // DTO 만들고
        SalesHeaderDTO dto = new SalesHeaderDTO(
                header.getOrderId(),
                header.getSalesDate(),
                header.getTotalAmount(),
                header.getSalesStatus()
        );

        // 나중에 아이템 붙이기
        List<SalesItemDTO> items = salesItemRepository.findItemsByHeaderId(orderId);
        dto.setSalesItems(items);

        return dto;
    }

    //보류 주문 취소
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        SalesHeader header = salesHeaderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("보류 주문 없음"));

        if (!SalesStatus.HOLD.equals(header.getSalesStatus())) {
            throw new RuntimeException("보류 상태가 아닌 주문은 취소 불가");
        }

        header.setSalesStatus(SalesStatus.CANCELLED);

        // 만약 보류 주문 취소 시 재고를 복구해야 한다면 여기서 재고 복구 처리
        // for (SalesItem item : header.getItems()) {
        //     STK stk = stkRepository.findByProductAndLot(...);
        //     stk.setQuantity(stk.getQuantity() + item.getSalesQuantity());
        //     stkRepository.save(stk);
        // }

        salesHeaderRepository.save(header);

    }
}
