package com.youthcase.orderflow.sd.sdSales.service;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.sd.sdSales.domain.*;
import com.youthcase.orderflow.sd.sdSales.dto.AddItemRequest;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Transactional
    public SalesHeader createOrder() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String lastOrderNo = salesHeaderRepository.findLastOrderNoByDate(datePrefix);

        int nextSeq = 1;
        if (lastOrderNo != null && lastOrderNo.length() > 9) {
            String seqStr = lastOrderNo.substring(9); // "20251012-005" → "005"
            nextSeq = Integer.parseInt(seqStr) + 1;
        }

        String newOrderNo = String.format("%s-%03d", datePrefix, nextSeq);

        SalesHeader header = new SalesHeader();
        header.setOrderNo(newOrderNo);
        header.setSalesDate(LocalDateTime.now());
        header.setSalesStatus(SalesStatus.PENDING);
        header.setTotalAmount(BigDecimal.ZERO);

        return salesHeaderRepository.save(header);
    }

    //상품추가
    @Override
    @Transactional
    public SalesItem addItemToOrder(AddItemRequest request) {
        SalesHeader header = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        Product product = productRepository.findByGtin(request.getGtin())
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        SalesItem item = new SalesItem();
        item.setSalesHeader(header);
        item.setProduct(product);
        item.setSalesQuantity(request.getQuantity());
        item.setSdPrice(request.getPrice());

        BigDecimal subtotal = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        item.setSubtotal(subtotal);

        header.setTotalAmount(header.getTotalAmount().add(subtotal));

        salesItemRepository.save(item);
        salesHeaderRepository.save(header);

        return item;
    }
    //salesHeader 바코드로 아이템 추가 + 재고수정 (react+vite 연동)
    @Override
    @Transactional
    public void confirmOrder(ConfirmOrderRequest request) {
        SalesHeader header = salesHeaderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 없음"));

        // ✅ 프론트에서 이미 재고 차감, SalesItem 추가 완료된 상태이므로
        // 서버에서는 단순히 상태만 "COMPLETED" 로 변경하면 됨
        header.setSalesStatus(SalesStatus.COMPLETED);
        header.setSalesDate(LocalDateTime.now());

        // ✅ 총금액이 프론트 계산 결과로 넘어올 경우 반영
        if (request.getTotalAmount() != null) {
            header.setTotalAmount(request.getTotalAmount());
        }

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
                header.getOrderNo(),
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
