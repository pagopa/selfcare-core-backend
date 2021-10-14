package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.dao.model.Product;
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


    public static Product toEntity(UpdateProductDto updateProductDto) {
        Product product = new Product();
        product.setLogo(updateProductDto.getLogo());
        product.setTitle(updateProductDto.getTitle());
        product.setDescription(updateProductDto.getDescription());
        product.setUrlPublic(updateProductDto.getUrlPublic());
        product.setUrlBO(updateProductDto.getUrlBO());

        return product;
    }

}
