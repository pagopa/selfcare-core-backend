package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateProductDto {

    @ApiModelProperty(value = "${swagger.product.model.logo}", required = true)
    @JsonProperty(required = true)
    private String logo;
    @ApiModelProperty(value = "${swagger.product.model.title}", required = true)
    @JsonProperty(required = true)
    private String title;
    @ApiModelProperty(value = "${swagger.product.model.description}", required = true)
    @JsonProperty(required = true)
    private String description;
    @ApiModelProperty(value = "${swagger.product.model.urlPublic}", required = true)
    @JsonProperty(required = true)
    private String urlPublic;
    @ApiModelProperty(value = "${swagger.product.model.urlBO}", required = true)
    @JsonProperty(required = true)
    private String urlBO;
    @ApiModelProperty(value = "${swagger.product.model.code}", required = true)
    @JsonProperty(required = true)
    private String code;//TODO add code attribute

}
