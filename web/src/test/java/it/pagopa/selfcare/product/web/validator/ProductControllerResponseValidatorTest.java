package it.pagopa.selfcare.product.web.validator;

import it.pagopa.selfcare.commons.web.validator.ControllerResponseValidator;
import it.pagopa.selfcare.commons.web.validator.PointcutControllerResponseValidatorBaseTest;
import it.pagopa.selfcare.product.web.controller.DummyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest(classes = {
        ValidationAutoConfiguration.class,
        DummyController.class,
        ProductControllerResponseValidator.class})
@EnableAspectJAutoProxy
class ProductControllerResponseValidatorTest extends PointcutControllerResponseValidatorBaseTest {

    @Autowired
    private DummyController controller;

    @SpyBean
    private ProductControllerResponseValidator validatorSpy;


    @Override
    protected ControllerResponseValidator getValidatorSpy() {
        return validatorSpy;
    }

    @Override
    protected void invokeNotVoidMethod() {
        controller.notVoidMethod();
    }

    @Override
    protected void invokeVoidMethod() {
        controller.voidMethod();
    }

}