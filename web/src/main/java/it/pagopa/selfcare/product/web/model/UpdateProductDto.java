package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateProductDto {

    @ApiModelProperty("${swagger.product.model.logo}")
    private String logo;
    @ApiModelProperty("${swagger.product.model.title}")
    private String title;
    @ApiModelProperty("${swagger.product.model.description}")
    private String description;
    @ApiModelProperty("${swagger.product.model.urlPublic}")
    private String urlPublic;
    @ApiModelProperty("${swagger.product.model.urlBO}")
    private String urlBO;
    @ApiModelProperty("${swagger.product.model.code}")
    private String code;

}
