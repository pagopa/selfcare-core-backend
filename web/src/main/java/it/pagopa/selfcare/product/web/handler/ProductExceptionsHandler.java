package it.pagopa.selfcare.product.web.handler;

import it.pagopa.selfcare.commons.web.model.ErrorResource;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.web.controller.ProductController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static it.pagopa.selfcare.commons.web.handler.RestExceptionsHandler.UNHANDLED_EXCEPTION;

/**
 * The Class RestExceptionsHandler.
 */
@ControllerAdvice(assignableTypes = ProductController.class)
@Slf4j
public class ProductExceptionsHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResource handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({InvalidRoleMappingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorResource handleInvalidRoleMappingException(InvalidRoleMappingException e) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }


    @ExceptionHandler({ResourceAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    ErrorResource handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }
}