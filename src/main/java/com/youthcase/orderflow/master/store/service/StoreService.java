package com.youthcase.orderflow.master.store.service;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.dto.StoreAdminRequestDTO;
import com.youthcase.orderflow.master.store.dto.StoreEnvRequestDTO;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import com.youthcase.orderflow.global.error.ResourceNotFoundException; // ✅ 추가
import com.youthcase.orderflow.master.store.service.kakaoapi.KakaoGeocodingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository repository;
    private final KakaoGeocodingClient kakaoClient; // ✅ 좌표 자동 변환용

    // ────────────────────────────────
    // 🔹 1. 관리자 전용: 신규 등록
    // ────────────────────────────────
    @Transactional
    public Store createByAdmin(StoreAdminRequestDTO dto) {
        if (repository.existsById(dto.getStoreId())) {
            throw new IllegalArgumentException("이미 존재하는 지점 코드입니다: " + dto.getStoreId());
        }

        Store store = dto.toEntity();

        // ✅ 주소 → 좌표 변환
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            var geo = kakaoClient.getCoordinate(dto.getAddress());
            if (geo != null) {
                store.setLatitude(geo.getLatitude());
                store.setLongitude(geo.getLongitude());
            }
        }

        return repository.save(store);
    }

    // ────────────────────────────────
    // 🔹 2. 관리자 전용: 전체 수정
    // ────────────────────────────────
    @Transactional
    public Store updateByAdmin(String storeId, StoreAdminRequestDTO dto) {
        Store store = repository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("지점을 찾을 수 없습니다: " + storeId)); // ✅ 404 처리

        store.setStoreName(dto.getStoreName());
        store.setBrandCode(dto.getBrandCode());
        store.setRegionCode(dto.getRegionCode());
        store.setStoreType(dto.getStoreType());
        store.setOpenDate(dto.getOpenDate());
        store.setManagerId(dto.getManagerId());
        store.setAddress(dto.getAddress());
        store.setAddressDetail(dto.getAddressDetail());
        store.setPostCode(dto.getPostCode());
        store.setOwnerName(dto.getOwnerName());
        store.setBizHours(dto.getBizHours());
        store.setContactNumber(dto.getContactNumber());
        store.setActive(dto.getActive());

        // ✅ 주소 변경 시 좌표 재변환
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            var geo = kakaoClient.getCoordinate(dto.getAddress());
            if (geo != null) {
                store.setLatitude(geo.getLatitude());
                store.setLongitude(geo.getLongitude());
            }
        }

        return repository.save(store);
    }

    // ────────────────────────────────
    // 🔹 3. ENV 권한 전용 (점장, 매니저): 위치, 운영환경 수정
    // ────────────────────────────────
    @Transactional
    public Store updateEnv(String storeId, StoreEnvRequestDTO dto) {
        Store store = repository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("지점을 찾을 수 없습니다: " + storeId)); // ✅ 404 처리

        store.setOwnerName(dto.getOwnerName());
        store.setBizHours(dto.getBizHours());
        store.setContactNumber(dto.getContactNumber());
        store.setActive(dto.getActive());

        // ✅ 주소가 수정되면 좌표 갱신
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            store.setAddress(dto.getAddress());
            store.setAddressDetail(dto.getAddressDetail());
            store.setPostCode(dto.getPostCode());
            var geo = kakaoClient.getCoordinate(dto.getAddress());
            if (geo != null) {
                store.setLatitude(geo.getLatitude());
                store.setLongitude(geo.getLongitude());
            }
        }

        return repository.save(store);
    }

    // ────────────────────────────────
    // 🔹 4. 조회 / 삭제
    // ────────────────────────────────
    @Transactional(readOnly = true)
    public List<Store> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Store findById(String storeId) {
        return repository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("지점을 찾을 수 없습니다: " + storeId)); // ✅ 404 처리
    }

    @Transactional
    public void delete(String storeId) {
        if (!repository.existsById(storeId)) {
            throw new ResourceNotFoundException("삭제할 지점을 찾을 수 없습니다: " + storeId); // ✅ 404 처리
        }
        repository.deleteById(storeId);
    }
}
