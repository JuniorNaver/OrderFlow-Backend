package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.PO;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.Status;

public interface POService {

    /** 발주 확정 처리 */
    PO confirmOrder(Long poId);

}
