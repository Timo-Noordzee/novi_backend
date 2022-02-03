package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BaseHttpException {

    public static final String ERROR_CODE = "entity-already-exists";

    public EntityAlreadyExistsException(final String id, final String entityType) {
        super(
                ERROR_CODE,
                HttpStatus.BAD_REQUEST,
                String.format("entity of type %s with id %s does already exist", entityType, id)
        );
    }
}
