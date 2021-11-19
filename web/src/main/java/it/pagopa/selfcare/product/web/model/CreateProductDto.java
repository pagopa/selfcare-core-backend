package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateProductDto {

    @ApiModelProperty(value = "${swagger.product.model.logo}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String logo;
    @ApiModelProperty(value = "${swagger.product.model.title}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String title;
    @ApiModelProperty(value = "${swagger.product.model.description}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;
    @ApiModelProperty(value = "${swagger.product.model.urlPublic}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String urlPublic;
    @ApiModelProperty(value = "${swagger.product.model.urlBO}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String urlBO;
    @ApiModelProperty(value = "${swagger.product.model.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String code;

}
