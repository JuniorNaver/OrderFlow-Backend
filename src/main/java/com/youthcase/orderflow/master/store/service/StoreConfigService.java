package com.youthcase.orderflow.master.store.service;

import com.youthcase.orderflow.master.store.dto.StoreConfigUpdateRequestDTO;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreConfigService {

    private final StoreRepository storeRepository;

    /**
     * 점포 운영환경 조회 (BI_003)
     * - BI 분석/경영지원 화면에서 지점 정보 확인용
     */
    public Store getStoreConfig(String storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("지점이 존재하지 않습니다."));
    }

    /**
     * 점포 운영환경 수정 (BI_003)
     * - 관리자 및 점장 공통 사용
     * - 기준정보 변경은 제외하고, 운영 관련 필드만 수정
     */
    @Transactional
    public Store updateStoreConfig(String storeId, StoreConfigUpdateRequestDTO dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("지점이 존재하지 않습니다."));

        store.setOwnerName(dto.getOwnerName());
        store.setBizHours(dto.getBizHours());
        store.setAddress(dto.getAddress());
        store.setAddressDetail(dto.getAddressDetail());
        store.setPostCode(dto.getPostCode());
        store.setActive(dto.getActive());
        store.setContactNumber(dto.getContactNumber());

        return storeRepository.save(store);
    }
}
