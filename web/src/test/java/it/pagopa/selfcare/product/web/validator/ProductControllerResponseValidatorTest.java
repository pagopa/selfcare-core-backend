package it.pagopa.selfcare.product.web.validator;

import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.web.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest(classes = {ProductController.class, ProductControllerResponseValidator.class})
@EnableAutoConfiguration
class ProductControllerResponseValidatorTest {

    @SpyBean
    private ProductControllerResponseValidator validatorSpy;

    @Autowired
    private ProductController controller;

    @MockBean
    private ProductService productServiceMock;

    @Test
    void controllersPointcut_returnNotVoid() {
        // given
        // when
        controller.getProduct("id");
        // then
        Mockito.verify(validatorSpy, Mockito.times(1))
                .validateResponse(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(validatorSpy);
    }

    @Test
    void controllersPointcut_returnVoid() {
        // given
        // when
        controller.deleteProduct("id");
        // then
        Mockito.verify(validatorSpy, Mockito.times(1))
                .validateResponse(Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(validatorSpy);
    }
}