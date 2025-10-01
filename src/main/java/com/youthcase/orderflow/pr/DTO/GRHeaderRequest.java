package com.youthcase.orderflow.pr.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GRHeaderRequest {

    @NotNull
    @Size(max = 10)
    private String status;

    @NotNull
    private BigDecimal totalAmount;

    @Size(max = 255)
    private String remarks;

    @NotNull
    private String poId;

    @NotNull
    private String userId;
}