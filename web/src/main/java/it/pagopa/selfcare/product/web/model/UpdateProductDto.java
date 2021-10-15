package it.pagopa.selfcare.product.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class UpdateProductDto {

    @Schema(description = "${swagger.product.model.logo}")
    private String logo;
    @Schema(description = "${swagger.product.model.title}")
    private String title;
    @Schema(description = "${swagger.product.model.description}")
    private String description;
    @Schema(description = "${swagger.product.model.urlPublic}")
    private String urlPublic;
    @Schema(description = "${swagger.product.model.urlBO}")
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
