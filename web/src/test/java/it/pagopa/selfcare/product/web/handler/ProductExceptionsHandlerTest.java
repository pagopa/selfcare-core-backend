package it.pagopa.selfcare.product.web.handler;

import it.pagopa.selfcare.commons.web.model.ErrorResource;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductExceptionsHandlerTest {

    private static final String DETAIL_MESSAGE = "detail message";

    private final ProductExceptionsHandler handler = new ProductExceptionsHandler();


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


    @Test
    void handleInvalidRoleMappingException() {
        // given
        InvalidRoleMappingException mockException = Mockito.mock(InvalidRoleMappingException.class);
        Mockito.when(mockException.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource response = handler.handleInvalidRoleMappingException(mockException);
        // then
        assertNotNull(response);
        assertEquals(DETAIL_MESSAGE, response.getMessage());
    }


    @Test
    void handleResourceAlreadyExistsException() {
        // given
        ResourceAlreadyExistsException mockException = Mockito.mock(ResourceAlreadyExistsException.class);
        Mockito.when(mockException.getMessage())
                .thenReturn(DETAIL_MESSAGE);
        // when
        ErrorResource response = handler.handleResourceAlreadyExistsException(mockException);
        // then
        assertNotNull(response);
        assertEquals(DETAIL_MESSAGE, response.getMessage());
    }

}