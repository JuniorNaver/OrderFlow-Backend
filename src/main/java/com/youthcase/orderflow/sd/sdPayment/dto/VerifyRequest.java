package com.youthcase.orderflow.sd.sdPayment.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
        String impUid,
        @NotBlank String merchantUid
) {}