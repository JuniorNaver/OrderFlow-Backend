package com.youthcase.orderflow.pr.task;

import com.youthcase.orderflow.bi.service.recommend.BIRecommendBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendUpdateJob {
    private final BIRecommendBatchService recommendBatchService;

    @Async
    public void trigger(String storeId) {
        try {
            String from = LocalDate.now().minusDays(7).format(DateTimeFormatter.BASIC_ISO_DATE);
            String to = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

            recommendBatchService.generateRecommendationsV2(
                    storeId, from, to,
                    Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()
            );

            log.info("[BIRecommend] 자동 추천 발주 갱신 완료 (storeId={})", storeId);

        } catch (Exception e) {
            // 비동기 작업은 실패해도 PR 흐름에 영향 없게만 처리
            if (e instanceof InterruptedException ie) {
                Thread.currentThread().interrupt(); // 인터럽트 보존
            }
            log.warn("[BIRecommend] 추천 발주 갱신 중 오류 (storeId={}): {}", storeId, e.getMessage(), e);
        }
    }
}
