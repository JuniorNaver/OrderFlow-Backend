package com.youthcase.orderflow.sd.sdPayment.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
        @NotBlank String impUid,
        @NotBlank String merchantUid
) {}