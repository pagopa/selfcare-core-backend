package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class CreateProductDto {

    @ApiModelProperty("${swagger.product.title}")
    private String title;
    @ApiModelProperty("${swagger.product.description}")
    private String description;


    public static Product toEntity(CreateProductDto createProductDto) {
        Product product = new Product();
        product.setTitle(createProductDto.getTitle());
        product.setDescription(createProductDto.getDescription());

        return product;
    }

}
