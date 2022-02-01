package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class GenerateInvoiceException extends BaseHttpException {

    public static final String ERROR_CODE = "generate-invoice";

    public GenerateInvoiceException(final String message) {
        super(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
