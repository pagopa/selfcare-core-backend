package it.pagopa.selfcare.product.core.exception;

import org.springframework.dao.DuplicateKeyException;

public class ResourceAlreadyExistsException extends DuplicateKeyException {

    public ResourceAlreadyExistsException(String msg) {
        super(msg);
    }

}
