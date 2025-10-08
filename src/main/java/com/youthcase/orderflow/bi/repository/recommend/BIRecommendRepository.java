/**
 * BIRecommendRepository
 * ----------------------
 * 🗃 BI_RECOMMEND_RESULT 테이블 접근 JPA 레포지토리.
 * - 기간/점포 기준 조회 메서드 제공.
 */
package com.youthcase.orderflow.bi.repository.recommend;

import com.youthcase.orderflow.bi.domain.recommend.BIRecommendResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BIRecommendRepository extends JpaRepository<BIRecommendResult, Long> {

    /**
     * 특정 점포의 추천 발주 결과를 기간별로 조회.
     * @param storeId 점포 ID
     * @param from    시작일(YYYYMMDD)
     * @param to      종료일(YYYYMMDD)
     */
    List<BIRecommendResult> findByStoreIdAndPeriodStartKeyGreaterThanEqualAndPeriodEndKeyLessThanEqual(
            Long storeId, String from, String to
    );
}
