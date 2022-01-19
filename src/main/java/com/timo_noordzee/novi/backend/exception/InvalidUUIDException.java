package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidUUIDException extends BaseHttpException {

    public static final String ERROR_CODE = "invalid-uuid";

    public InvalidUUIDException(final String id) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("%s isn't a valid UUID", id));
    }
}
