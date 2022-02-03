package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class FileUploadException extends BaseHttpException {

    public static final String ERROR_CODE = "file-upload-exception";

    public FileUploadException(final String message) {
        super(ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
