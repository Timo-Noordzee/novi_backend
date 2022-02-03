package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class UnknownRepairLineTypeException extends BaseHttpException {

    public static final String ERROR_CODE = "unknown-repair-line-type";

    public UnknownRepairLineTypeException(final int type) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("type %d is unknown", type));
    }
}
