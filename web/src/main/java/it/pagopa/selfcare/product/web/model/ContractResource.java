package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.ContractOperations;
import lombok.Data;

import java.time.Instant;

@Data
public class ContractResource implements ContractOperations {

    @ApiModelProperty(value = "${swagger.product.model.contractTemplatePath}")
    private String contractTemplatePath;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateVersion}")
    private String contractTemplateVersion;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateUpdateDateTime}")
    private Instant contractTemplateUpdatedAt;

}
