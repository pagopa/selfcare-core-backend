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
import javax.validation.constraints.NotEmpty;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateProductDtoTest {
    private Validator validator;

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
        toCheckMap.put("title", NotBlank.class);
        toCheckMap.put("description", NotBlank.class);
        toCheckMap.put("contractTemplatePath", NotBlank.class);
        toCheckMap.put("contractTemplateVersion", NotBlank.class);
        toCheckMap.put("identityTokenAudience", NotBlank.class);
        toCheckMap.put("urlBO", NotBlank.class);
        toCheckMap.put("roleMappings", NotEmpty.class);
        toCheckMap.put("backOfficeEnvironmentConfigurations", NotEmpty.class);
        CreateProductDto createProductDto = new CreateProductDto();
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(createProductDto);
        // then
        assertFalse(violations.isEmpty());
        List<ConstraintViolation<Object>> filteredViolations = violations.stream()
                .filter(violation -> {
                    Class<? extends Annotation> annotationToCheck = toCheckMap.get(violation.getPropertyPath().toString());
                    return !violation.getConstraintDescriptor().getAnnotation().annotationType().equals(annotationToCheck);
                })
                .collect(Collectors.toList());
        assertTrue(filteredViolations.isEmpty(), filteredViolations.toString());
    }

    @Test
    void validateNotNullFields() {
        // given
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            productRoleInfo.setMultiroleAllowed(true);
            roleMappings.put(partyRole, productRoleInfo);
        }
        UpdateProductDto product = mockInstance(new UpdateProductDto(), "setRoleMappings", "setBackOfficeEnvironmentConfigurations");
        product.setRoleMappings(roleMappings);
        product.setBackOfficeEnvironmentConfigurations(Map.of("test", mockInstance(new BackOfficeConfigurationsResource())));
        product.setLogoBgColor("#FF2354");
        // when
        Set<ConstraintViolation<Object>> violations = validator.validate(product);
        // then
        assertTrue(violations.isEmpty(), violations.toString());
    }
}