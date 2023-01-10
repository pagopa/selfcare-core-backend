package it.pagopa.selfcare.product.connector.model;

import java.time.Instant;

public interface ContractOperations {

    Instant getContractTemplateUpdatedAt();

    void setContractTemplateUpdatedAt(Instant contractTemplateUpdatedAt);

    String getContractTemplatePath();

    void setContractTemplatePath(String contractTemplatePath);

    String getContractTemplateVersion();

    void setContractTemplateVersion(String contractTemplateVersion);

}
