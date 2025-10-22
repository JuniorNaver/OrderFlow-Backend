package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.PO;

public interface POService {

    /** 발주 확정 처리 */
    PO confirmOrder(Long poId);

    void updateReceiveProgress(Long poId);

}
