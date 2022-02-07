package it.pagopa.selfcare.product.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/core-config.properties")
class CoreConfig {
}
