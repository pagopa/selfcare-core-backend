package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class ProductResource {

    @ApiModelProperty("${swagger.product.id}")
    private String id;
    @ApiModelProperty("${swagger.product.title}")
    private String title;
    @ApiModelProperty("${swagger.product.description}")
    private String description;


    public static ProductResource create(Product product) {
        ProductResource resource = new ProductResource();
        resource.setId(product.getId());
        resource.setTitle(product.getTitle());
        resource.setDescription(product.getDescription());

        return resource;
    }

}
