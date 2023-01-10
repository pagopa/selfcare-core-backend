package it.pagopa.selfcare.product.connector.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Contract implements ContractOperations {
    private Instant contractTemplateUpdatedAt;
    private String contractTemplatePath;
    private String contractTemplateVersion;
}
