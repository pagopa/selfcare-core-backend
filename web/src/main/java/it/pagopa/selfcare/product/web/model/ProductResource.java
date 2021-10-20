package it.pagopa.selfcare.product.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class ProductResource {

    @Schema(description = "${swagger.product.id}")
    private String id;
    @Schema(description = "${swagger.product.logo}")
    private String logo;
    @Schema(description = "${swagger.product.title}")
    private String title;
    @Schema(description = "${swagger.product.description}")
    private String description;
    @Schema(description = "${swagger.product.urlPublic}")
    private String urlPublic;
    @Schema(description = "${swagger.product.urlBO}")
    private String urlBO;


    public static ProductResource create(Product product) {
        ProductResource resource = new ProductResource();
        resource.setId(product.getId());
        resource.setLogo(product.getLogo());
        resource.setTitle(product.getTitle());
        resource.setDescription(product.getDescription());
        resource.setUrlPublic(product.getUrlPublic());
        resource.setUrlBO(product.getUrlBO());

        return resource;
    }

}
