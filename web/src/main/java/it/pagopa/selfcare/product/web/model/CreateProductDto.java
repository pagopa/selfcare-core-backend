package it.pagopa.selfcare.product.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.product.dao.model.Product;
import lombok.Data;

@Data
public class CreateProductDto {

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
