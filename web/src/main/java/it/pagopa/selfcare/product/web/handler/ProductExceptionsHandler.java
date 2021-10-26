package it.pagopa.selfcare.product.web.handler;

import feign.FeignException;
import it.pagopa.selfcare.commons.web.model.ErrorResource;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.web.ProductController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException e) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus httpStatus = HttpStatus.resolve(e.status());

        if (e.contentUTF8() != null && e.contentUTF8().startsWith("{\"returnMessages\":")
                && httpStatus != null && httpStatus.is4xxClientError()) {
            log.warn(UNHANDLED_EXCEPTION, e);
        } else {
            log.error(UNHANDLED_EXCEPTION, e);
        }
        return new ResponseEntity<>(e.contentUTF8(), httpHeaders, httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    private ErrorResource handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn(UNHANDLED_EXCEPTION, e);
        return new ErrorResource(e.getMessage());
    }
}