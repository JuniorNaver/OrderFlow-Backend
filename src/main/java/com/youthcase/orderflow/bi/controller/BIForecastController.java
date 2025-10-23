/**
 * BIForecastController
 * ---------------------
 * 🌍 React 프론트엔드에서 예측 결과 조회 요청을 처리하는 REST API 컨트롤러.
 * - URL: /bi/forecast
 * - Method: GET
 * - Params: storeId, from, to
 */
package com.youthcase.orderflow.bi.controller;

import com.youthcase.orderflow.bi.dto.ForecastDTO;
import com.youthcase.orderflow.bi.service.forecast.BIForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/forecast")
@RequiredArgsConstructor
public class BIForecastController {

    private final BIForecastService forecastService;

    /**
     * 특정 점포의 기간별 예측 판매량 조회
     */
    @GetMapping
    public ResponseEntity<List<ForecastDTO>> getForecasts(
            @RequestParam String storeId,
            @RequestParam String from,
            @RequestParam String to
    ) {
        return ResponseEntity.ok(forecastService.getForecasts(storeId, from, to));
    }
}
