package it.pagopa.selfcare.product.web.model;

import it.pagopa.selfcare.commons.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProductResourceTest {

    private Validator validator;
    private static final ProductResource PRODUCT_RESOURCE = TestUtils.mockInstance(new ProductResource());

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validateNullFields() {
        // given
        HashMap<String, Class<? extends Annotation>> toCheckMap = new HashMap<>();
        toCheckMap.put("id", NotBlank.class);
        toCheckMap.put("logo", NotBlank.class);
        toCheckMap.put("title", NotBlank.class);
        toCheckMap.put("description", NotBlank.class);
        toCheckMap.put("urlPublic", NotBlank.class);
        toCheckMap.put("urlBO", NotBlank.class);
        toCheckMap.put("creationDateTime", NotNull.class);
        toCheckMap.put("code", NotBlank.class);
        ProductResource productResource = new ProductResource();
        productResource.setId(null);
        productResource.setLogo(null);
        productResource.setTitle(null);
        productResource.setDescription(null);
        productResource.setUrlPublic(null);
        productResource.setUrlBO(null);
        productResource.setCreationDateTime(null);
        productResource.setCode(null);
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(productResource);
        // then
        List<ConstraintViolation<Object>> filteredViolations = violations.stream()
                .filter(violation -> {
                    Class<? extends Annotation> annotationToCheck = toCheckMap.get(violation.getPropertyPath().toString());
                    return !violation.getConstraintDescriptor().getAnnotation().annotationType().equals(annotationToCheck);
                })
                .collect(Collectors.toList());
        assertTrue(filteredViolations.isEmpty());
    }

    @Test
    void validateNotNullFields() {
        // given
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(PRODUCT_RESOURCE);
        // then
        assertTrue(violations.isEmpty());
    }

}