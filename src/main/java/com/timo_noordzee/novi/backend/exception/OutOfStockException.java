package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class OutOfStockException extends BaseHttpException {

    public static final String ERROR_CODE = "out-of-stock";

    public OutOfStockException(final UUID partId, final int stock, final int amount) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("current stock of %s is %d while trying to decrement by %d", partId.toString(), stock, amount));
    }
}
