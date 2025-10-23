/**
 * BIRecommendService
 * -------------------
 * 💡 추천 발주 결과 조회 담당 서비스.
 * - 저장된 추천 결과를 DTO로 변환하여 반환.
 */
package com.youthcase.orderflow.bi.service.recommend;

import com.youthcase.orderflow.bi.dto.RecommendDTO;
import com.youthcase.orderflow.bi.mapper.BIRecommendMapper;
import com.youthcase.orderflow.bi.repository.recommend.BIRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BIRecommendService {

    private final BIRecommendRepository repository;
    private final BIRecommendMapper mapper;

    /**
     * 특정 기간의 추천 발주 결과 조회
     */
    public List<RecommendDTO> getRecommendations(String storeId, String from, String to) {
        return repository.findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(storeId, from, to)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
