package it.pagopa.selfcare.product.connector.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
