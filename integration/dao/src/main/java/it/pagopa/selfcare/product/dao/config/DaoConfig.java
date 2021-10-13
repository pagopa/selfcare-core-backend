package it.pagopa.selfcare.product.dao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/dao-config.properties")
public class DaoConfig {
}
