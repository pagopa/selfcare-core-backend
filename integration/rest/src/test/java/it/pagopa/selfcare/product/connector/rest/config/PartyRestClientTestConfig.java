package it.pagopa.selfcare.product.connector.rest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(PartyRestClientConfig.class)
public class PartyRestClientTestConfig {

}