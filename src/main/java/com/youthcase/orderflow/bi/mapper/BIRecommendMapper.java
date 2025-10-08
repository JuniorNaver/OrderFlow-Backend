/**
 * BIRecommendMapper
 * ------------------
 * Entity ↔ DTO 변환 전담 매퍼
 * - ModelMapper를 이용해 공통 필드 자동 매핑.
 * - 필요 시 커스텀 매핑(상품명, 카테고리명 등) 확장 가능.
 */
package com.youthcase.orderflow.bi.mapper;

import com.youthcase.orderflow.bi.domain.recommend.BIRecommendResult;
import com.youthcase.orderflow.bi.dto.RecommendDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BIRecommendMapper {

    private final ModelMapper mapper;

    public BIRecommendMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    /** Entity → DTO 변환 */
    public RecommendDTO toDto(BIRecommendResult entity) {
        return mapper.map(entity, RecommendDTO.class);
    }
}
