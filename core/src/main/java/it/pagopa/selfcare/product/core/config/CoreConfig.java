package it.pagopa.selfcare.product.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties({LogoImageProperties.class, DepictImageProperties.class})
@PropertySource("classpath:config/core-config.properties")
class CoreConfig {
}
