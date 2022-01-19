package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class UnknownRoleException extends BaseHttpException {

    public static final String ERROR_CODE = "unknown-role";

    public UnknownRoleException(final String role) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("role %s is unknown", role));
    }

}
