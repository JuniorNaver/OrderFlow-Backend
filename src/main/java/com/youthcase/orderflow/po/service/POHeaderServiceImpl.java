package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class POHeaderServiceImpl implements POHeaderService {
    private final POHeaderRepository poHeaderRepository;

    /** 모든 발주 헤더 조회 */
    @Override
    public List<POHeaderResponseDTO> findAll() {
        return poHeaderRepository.findAll().stream()
                .map(this::toDto) //현재 클래스에 정의된 toDto() 메서드를 가리킴
                .toList();
    }

    /** 장바구니 저장*/
    @Override
    public void updateStatusToSaved(Long poId) {
        POHeader header = poHeaderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 발주 헤더가 존재하지 않습니다."));

        header.setStatus(POStatus.S);
        poHeaderRepository.save(header);
    }

    private POHeaderResponseDTO toDto(POHeader poHeader) {
        return POHeaderResponseDTO.builder()
                .poId(poHeader.getPoId())
                .status(poHeader.getStatus())
                .totalAmount(poHeader.getTotalAmount())
                .actionDate(poHeader.getActionDate())
                .remarks(poHeader.getRemarks())
                .build();
    }
}



