package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class UpdateProductDto {

    @ApiModelProperty("${swagger.product.logo}")
    private String logo;
    @ApiModelProperty("${swagger.product.title}")
    private String title;
    @ApiModelProperty("${swagger.product.description}")
    private String description;
    @ApiModelProperty("${swagger.product.urlPublic}")
    private String urlPublic;
    @ApiModelProperty("${swagger.product.urlBO}")
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
