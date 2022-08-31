package it.pagopa.selfcare.product.web.handler;

import it.pagopa.selfcare.commons.web.model.Problem;
import it.pagopa.selfcare.commons.web.model.mapper.ProblemMapper;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.web.controller.ProductController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * The Class RestExceptionsHandler.
 */
@ControllerAdvice(assignableTypes = ProductController.class)
@Slf4j
public class ProductExceptionsHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    ResponseEntity<Problem> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn(e.toString());
        return ProblemMapper.toResponseEntity(new Problem(NOT_FOUND, e.getMessage()));
    }


    @ExceptionHandler({InvalidRoleMappingException.class})
    ResponseEntity<Problem> handleInvalidRoleMappingException(InvalidRoleMappingException e) {
        log.warn(e.toString());
        return ProblemMapper.toResponseEntity(new Problem(BAD_REQUEST, e.getMessage()));
    }


    @ExceptionHandler({ResourceAlreadyExistsException.class})
    ResponseEntity<Problem> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        log.warn(e.toString());
        return ProblemMapper.toResponseEntity(new Problem(CONFLICT, e.getMessage()));
    }

}