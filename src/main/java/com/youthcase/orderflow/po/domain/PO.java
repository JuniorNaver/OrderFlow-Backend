package com.youthcase.orderflow.po.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

// DTO 역할

@Data
@AllArgsConstructor
public class PO {
    private POHeader header;
    private List<POItem> items;
}

