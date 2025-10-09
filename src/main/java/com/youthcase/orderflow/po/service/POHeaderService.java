package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.dto.POHeaderResponseDTO;
import java.util.List;

public interface POHeaderService {

    // 모든 발주 헤더 조회
    List<POHeaderResponseDTO> findAll();
}