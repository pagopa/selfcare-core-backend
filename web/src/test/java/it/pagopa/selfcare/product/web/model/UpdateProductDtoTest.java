package it.pagopa.selfcare.product.web.model;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateProductDtoTest {

    private Validator validator;
    private static final UpdateProductDto UPDATE_PRODUCT_DTO = TestUtils.mockInstance(new UpdateProductDto());

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validateNullFields() {
        // given
        HashMap<String, Class<? extends Annotation>> toCheckMap = new HashMap<>();
        toCheckMap.put("logo", NotBlank.class);
        toCheckMap.put("title", NotBlank.class);
        toCheckMap.put("description", NotBlank.class);
        toCheckMap.put("urlBO", NotBlank.class);
        toCheckMap.put("roleMappings", NotNull.class);
        toCheckMap.put("contractTemplatePath", NotBlank.class);
        toCheckMap.put("contractTemplateVersion", NotBlank.class);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setLogo(null);
        updateProductDto.setTitle(null);
        updateProductDto.setDescription(null);
        updateProductDto.setUrlPublic(null);
        updateProductDto.setUrlBO(null);
        updateProductDto.setRoleMappings(null);
        updateProductDto.setContractTemplatePath(null);
        updateProductDto.setContractTemplateVersion(null);
        updateProductDto.setRoleManagementURL(null);
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(updateProductDto);
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
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        UPDATE_PRODUCT_DTO.setRoleMappings(map);
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(UPDATE_PRODUCT_DTO);
        // then
        assertTrue(violations.isEmpty());
    }

}