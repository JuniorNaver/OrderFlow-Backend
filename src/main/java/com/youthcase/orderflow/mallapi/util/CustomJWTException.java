package com.youthcase.orderflow.mallapi.util;

import java.io.Serial;

public class CustomJWTException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CustomJWTException(String message) {
        super(message);
    }
}
