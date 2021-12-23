package it.pagopa.selfcare.product.web.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(ProductSecurityConfig.class)
public class SecurityTestConfig {
}