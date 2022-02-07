package it.pagopa.selfcare.product.core.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(CoreConfig.class)
public class CoreTestConfig {
}