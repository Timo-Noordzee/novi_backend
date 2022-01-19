package com.timo_noordzee.novi.backend.controller;

import com.timo_noordzee.novi.backend.domain.HttpErrorResponse;
import com.timo_noordzee.novi.backend.exception.BaseHttpException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BaseHttpException.class)
    public ResponseEntity<HttpErrorResponse> handleException(final BaseHttpException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(exception.getBody());
    }
}
