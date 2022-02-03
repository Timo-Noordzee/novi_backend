package com.timo_noordzee.novi.backend.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseHttpException {

    public static final String ERROR_CODE = "entity-not-found";

    public EntityNotFoundException(final String id, final String entityType) {
        super(
                ERROR_CODE,
                HttpStatus.NOT_FOUND,
                String.format("entity of type %s with id %s doesn't exist", entityType, id)
        );
    }

    public EntityNotFoundException(final String parentId, final String parentEntityType, final String id, final String entityType) {
        super(
                ERROR_CODE,
                HttpStatus.NOT_FOUND,
                String.format("entity of type %s with id %s doesn't exist for parent entity of type %s with id %s", entityType, id, parentEntityType, parentId)
        );
    }
}
