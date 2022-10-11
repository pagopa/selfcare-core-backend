package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.BackOfficeConfigurations;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BackOfficeConfigurationsResource implements BackOfficeConfigurations {

    @ApiModelProperty(value = "${swagger.product.model.urlBO}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String url;

    @ApiModelProperty(value = "${swagger.product.model.identityTokenAudience}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String identityTokenAudience;

}