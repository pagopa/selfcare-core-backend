package it.pagopa.selfcare.product.core.exception;

public class InvalidRoleMappingException extends RuntimeException {

    public InvalidRoleMappingException(String message) {
        this(message, null);
    }

    public InvalidRoleMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
