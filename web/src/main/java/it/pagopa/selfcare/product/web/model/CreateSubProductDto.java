package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateSubProductDto {
    @ApiModelProperty(value = "${swagger.product.model.contractTemplatePath}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String contractTemplatePath;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateVersion}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String contractTemplateVersion;

    @ApiModelProperty(value = "${swagger.product.model.id}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "${swagger.product.model.title}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "${swagger.product.model.parentProduct}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String parent;
}
