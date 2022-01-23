package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenFileTypeException extends BaseHttpException {

    public static final String ERROR_CODE = "forbidden-file-type";

    public ForbiddenFileTypeException(final String fileType) {
        super(ERROR_CODE, HttpStatus.BAD_REQUEST, String.format("uploading a file of type %s isn't allowed", fileType));
    }
}
