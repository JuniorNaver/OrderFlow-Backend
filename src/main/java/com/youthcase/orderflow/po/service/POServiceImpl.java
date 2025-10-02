package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.PO;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.Status;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.po.repository.POItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class POServiceImpl implements POService {

    private final POHeaderRepository poHeaderRepository;
    private final POItemRepository poItemRepository;

    @Override
    public PO confirmOrder(Long poId) {
        // 1. 발주 헤더 조회
        POHeader poHeader = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new EntityNotFoundException("발주를 찾을 수 없습니다. ID=" + poId));

        // 2. 상태를 'PO' 로 변경
        poHeader.setStatus(Status.PO);

        // 3. 저장
        POHeader savedHeader = poHeaderRepository.save(poHeader);

        // 4. 관련된 아이템 조회
        List<POItem> items = poItemRepository.findByPoHeader_PoId(savedHeader.getPoId());

        // 5. 본사로 전송 (추후 실제 구현)
        sendToHQ(savedHeader, items);

        // 6. DTO 로 묶어서 리턴
        return new PO(savedHeader, items);
    }

    // 본사로 발주 정보 전송 (실제 구현은 RestTemplate, Kafka, MQ 등으로 연결 가능)
    private void sendToHQ(POHeader poHeader, List<POItem> items) {
        // TODO: 본사 시스템 연동 로직 작성
        System.out.println("본사로 발주 전송: HeaderID=" + poHeader.getPoId() + ", Items=" + items.size());
    }

}



