package it.pagopa.selfcare.product.connector.model;

import lombok.Data;

import java.time.Instant;

@Data
public class DummyContract implements ContractOperations {

    private String contractTemplatePath;
    private String contractTemplateVersion;
    private Instant contractTemplateUpdatedAt;
}
