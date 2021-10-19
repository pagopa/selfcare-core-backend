package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductResource {

    @ApiModelProperty("${swagger.product.model.id}")
    private String id;
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

}
