package it.pagopa.selfcare.product.web.handler;

import feign.FeignException;
import it.pagopa.selfcare.commons.web.model.ErrorResource;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductExceptionsHandlerTest {

    private static final String DETAIL_MESSAGE = "detail message";

    private final ProductExceptionsHandler handler = new ProductExceptionsHandler();


    @Test
    void handleFeignException_4xxStatus() {
        // given
        FeignException mockException = Mockito.mock(FeignException.class);
        Mockito.when(mockException.status())
                .thenReturn(HttpStatus.FORBIDDEN.value());
        // when
        ResponseEntity<ErrorResource> response = handler.handleFeignException(mockException);
        // then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    void handleFeignException_5xxStatus() {
        // given
        FeignException mockException = Mockito.mock(FeignException.class);
        Mockito.when(mockException.status())
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        // when
        ResponseEntity<ErrorResource> response = handler.handleFeignException(mockException);
        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void handleFeignException_invalidStatus() {
        // given
        FeignException mockException = Mockito.mock(FeignException.class);
        Mockito.when(mockException.status())
                .thenReturn(0);
        // when
        ResponseEntity<ErrorResource> response = handler.handleFeignException(mockException);
        // then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void handleResourceNotFoundException() {
        // given
        ResourceNotFoundException mockException = Mockito.mock(ResourceNotFoundException.class);
        Mockito.when(mockException.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource response = handler.handleResourceNotFoundException(mockException);
        // then
        assertNotNull(response);
        assertEquals(DETAIL_MESSAGE, response.getMessage());
    }

}