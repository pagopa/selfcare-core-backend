package it.pagopa.selfcare.product.core.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
