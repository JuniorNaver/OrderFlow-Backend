package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class POHeaderServiceImpl implements POHeaderService {
    private final POHeaderRepository poHeaderRepository;

    @Override
    public List<POHeaderResponseDTO> findAll() {
        return poHeaderRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private POHeaderResponseDTO toDto(POHeader poHeader) {
        return POHeaderResponseDTO.builder()
                .poId(poHeader.getPoId())
                .status(poHeader.getStatus())
                .totalAmount(poHeader.getTotalAmount())
                .actionDate(poHeader.getActionDate())
                .remarks(poHeader.getRemarks())
                .username(poHeader.getUser().getUsername()) // User 엔티티에 username 필드 있다고 가정
                .build();
    }
}



