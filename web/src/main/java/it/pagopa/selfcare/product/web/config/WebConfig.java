package it.pagopa.selfcare.product.web.config;

import it.pagopa.selfcare.commons.web.config.BaseWebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/web-config.properties")
@Import(BaseWebConfig.class)
class WebConfig {
}
