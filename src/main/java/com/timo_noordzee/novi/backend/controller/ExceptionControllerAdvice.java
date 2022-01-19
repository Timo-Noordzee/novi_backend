package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.domain.HttpErrorResponse;
import com.timo_noordzee.novi.backend.exception.BaseHttpException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseHttpException.class)
    public ResponseEntity<HttpErrorResponse> handleException(final BaseHttpException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(exception.getBody());
    }

}
