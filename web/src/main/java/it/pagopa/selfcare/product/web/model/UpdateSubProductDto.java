package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.InstitutionType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class UpdateSubProductDto {
    @ApiModelProperty(value = "${swagger.product.model.contractTemplatePath}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String contractTemplatePath;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateVersion}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String contractTemplateVersion;

    @ApiModelProperty(value = "${swagger.product.model.institutionContractMappings}")
    @Valid
    private Map<InstitutionType, ContractResource> institutionContractMappings;


    @ApiModelProperty(value = "${swagger.product.model.title}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String title;
}
