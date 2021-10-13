package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class CreateProductDto {

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


    public static Product toEntity(CreateProductDto createProductDto) {
        Product product = new Product();
        product.setLogo(createProductDto.getLogo());
        product.setTitle(createProductDto.getTitle());
        product.setDescription(createProductDto.getDescription());
        product.setUrlPublic(createProductDto.getUrlPublic());
        product.setUrlBO(createProductDto.getUrlBO());

        return product;
    }

}
