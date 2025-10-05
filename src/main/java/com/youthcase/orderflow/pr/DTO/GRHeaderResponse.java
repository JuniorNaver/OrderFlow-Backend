package com.youthcase.orderflow.pr.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class GRHeaderResponse {

    private String grId;
    private String status;
    private BigDecimal totalAmount;
    private String remarks;
    private Date approvalDate;
    private String poId;
    private String userId;
}