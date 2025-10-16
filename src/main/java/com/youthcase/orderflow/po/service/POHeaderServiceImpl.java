package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class POHeaderServiceImpl implements POHeaderService {
    private final POHeaderRepository poHeaderRepository;


    /** PO 생성 */
    @Override
    @Transactional
    public Long createNewPO() {
        POHeader poHeader = new POHeader();
        poHeader.setStatus(POStatus.PR); // 'PR' = 초안 상태
        poHeaderRepository.save(poHeader);
        return poHeader.getPoId();
    }

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

    /** 저장된 장바구니 불러오기 */
    @Override
    public List<POHeaderResponseDTO> getSavedCartList(){
        List<POHeader> savedHeaders = poHeaderRepository.findByStatus(POStatus.S);

        return savedHeaders.stream()
                .map(POHeaderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    };

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



