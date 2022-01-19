package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class EmailTakenException extends BaseHttpException {

    public static final String ERROR_CODE = "email-already-taken";

    public EmailTakenException(final String email) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("email address %s is already taken", email));
    }
}
