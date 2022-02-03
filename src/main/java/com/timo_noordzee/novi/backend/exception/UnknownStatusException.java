package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class UnknownStatusException extends BaseHttpException {

    public static final String ERROR_CODE = "unknown-status";

    public UnknownStatusException(final String status) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("status %s doesn't exist", status));
    }
}
