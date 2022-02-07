package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.List;

@Data
public class CreateProductDto {

    @ApiModelProperty(value = "${swagger.product.model.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "${swagger.product.model.title}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "${swagger.product.model.description}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "${swagger.product.model.urlPublic}", required = true)
    private String urlPublic;

    @ApiModelProperty(value = "${swagger.product.model.urlBO}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String urlBO;

    @ApiModelProperty(value = "${swagger.product.model.roleMappings}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private EnumMap<PartyRole, List<String>> roleMappings;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplatePath}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String contractTemplatePath;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateVersion}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String contractTemplateVersion;

    @ApiModelProperty(value = "${swagger.product.model.roleManagementURL}", required = true)
    private String roleManagementURL;

}
