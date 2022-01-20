package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class LicenseTakenException extends BaseHttpException {

    public static final String ERROR_CODE = "license-already-taken";

    public LicenseTakenException(final String license) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("license %s is already taken", license));
    }
}
