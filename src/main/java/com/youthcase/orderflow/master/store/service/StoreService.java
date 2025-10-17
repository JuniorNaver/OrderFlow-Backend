package com.youthcase.orderflow.master.store.service;

import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository repository;

    /**
     * 1️⃣ 지점 등록 (초기 설정 - 관리자 전용)
     * 이미 등록된 ID면 예외 발생
     */
    public Store create(Store request) {
        if (repository.existsByStoreId(request.getStoreId())) {
            throw new IllegalStateException("이미 등록된 지점입니다.");
        }
        return repository.save(request);
    }

    /**
     * 2️⃣ 지점 전체 조회
     */
    public List<Store> findAll() {
        return repository.findAll();
    }

    /**
     * 3️⃣ 지점 단일 조회
     */
    public Store findById(String storeId) {
        return repository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("지점을 찾을 수 없습니다."));
    }

    /**
     * 4️⃣ 지점 삭제 (관리자 전용)
     */
    public void delete(String storeId) {
        if (!repository.existsByStoreId(storeId)) {
            throw new IllegalArgumentException("삭제할 지점이 존재하지 않습니다.");
        }
        repository.deleteById(storeId);
    }
}
