/**
 * BIForecastMapper
 * ----------------
 * 📦 Entity ↔ DTO 간 변환 전담 컴포넌트.
 * - ModelMapper를 이용해 필드 매핑 자동화.
 */
package com.youthcase.orderflow.bi.mapper;

import com.youthcase.orderflow.bi.domain.forecast.BIForecastResult;
import com.youthcase.orderflow.bi.dto.ForecastDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BIForecastMapper {

    private final ModelMapper mapper;

    public BIForecastMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    /** Entity → DTO 변환 */
    public ForecastDTO toDto(BIForecastResult entity) {
        return mapper.map(entity, ForecastDTO.class);
    }
}
